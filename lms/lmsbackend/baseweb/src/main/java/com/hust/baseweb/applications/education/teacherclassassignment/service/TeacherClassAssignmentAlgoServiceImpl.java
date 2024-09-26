package com.hust.baseweb.applications.education.teacherclassassignment.service;

import com.hust.baseweb.applications.education.teacherclassassignment.model.*;
import com.hust.baseweb.applications.education.teacherclassassignment.service.algorithm.solver.BaseSolver;
import com.hust.baseweb.applications.education.teacherclassassignment.utils.TimetableConflictChecker;
import lombok.extern.log4j.Log4j2;
import lombok.var;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service cung cap cac thuat toan giai quyet bai toan Phan cong giang day.
 */
@Log4j2
@Service
public class TeacherClassAssignmentAlgoServiceImpl implements TeacherClassAssignmentAlgoService {

    // Kha nang rat cao la bo
//    private CheckConflict checker;
//
//    public TeacherClassAssignmentAlgoServiceImpl() {
//        checker = new CheckConflict();
//    }

    public String name() {
        return "TeacherClassAssignmentAlgoServiceImpl";
    }

    @Override
    public TeacherClassAssignmentOM computeTeacherClassAssignment(AlgoTeacherAssignmentIM input) {
        // Only for logging
        String thisMethodName = "computeTeacherClassAssignment, ";

        // Include all of (have INVALID timetable and NOT in preAssignments)
        // or (algo cannot assign that classes because of violating constraints)
        List<AlgoClassIM> notAssignedClasses = new ArrayList<>();

        // Collect classes (have INVALID timetable and NOT in preAssignments)
        List<AlgoClassIM> validClasses = new ArrayList<>();
        List<HashSet<Integer>> daysOfValidClass = new ArrayList<>();
        Set<String> preAssignedClassIds = Arrays
            .stream(input.getPreAssignments())
            .map(assignment -> assignment.getAlgoClassIM().getClassId())
            .collect(Collectors.toSet());

        for (AlgoClassIM algoClass : input.getClasses()) {
            HashSet<Integer> days = TimetableConflictChecker.extractDayOfTimeTable(algoClass.getTimetable());

            if (null != days || preAssignedClassIds.contains(algoClass.getClassId())) { // valid
                validClasses.add(algoClass);
                daysOfValidClass.add(days);
            } else { // invalid
                notAssignedClasses.add(algoClass);
//                log.info("TeacherClassAssignmentAlgoServiceImpl::computeTeacherClassAssignment, "
//                         + "detect invalid timetable class with classId = " + algoClass.getClassId());
            }
        }

        log.info(thisMethodName + "after removing classes (have INVALID timetable and NOT in preAssignments)"
                 + ", solve the problem with " + validClasses.size() + " classes");

        // Init
        AlgoClassIM[] algoClasses = validClasses.toArray(new AlgoClassIM[0]);
        AlgoTeacherIM[] algoTeachers = input.getTeachers();
        TeacherClassAssignmentModel[] preAssignments = input.getPreAssignments();

        int n = algoClasses.length; // number of classes after preprocessing;
        int m = algoTeachers.length; // number of teachers;
        double[] hourClass; // hourClass[i] is the number of hours of class i
        double[] maxHourTeacher; // maxHourTeacher[i] is the upper bound of the total hourLoad of classes assigned to teacher i

        // Encode data
        //// Encode teacher.
        HashMap<String, Integer> mTeacherId2Index = new HashMap<>();
        HashSet<Integer> teachersWantToMinimizeWorkingDays = new HashSet<>();
        for (int i = 0; i < m; i++) {
            AlgoTeacherIM teacher = algoTeachers[i];
            mTeacherId2Index.put(teacher.getId(), i);

            if (teacher.isMinimizeNumberWorkingDays()) {
                teachersWantToMinimizeWorkingDays.add(i);
            }

//            log.info("map: teacher[" + i + "] = " + teacher.getId());
        }

        //// Encode class
        HashMap<String, Integer> mClassId2Index = new HashMap<>();
        for (int i = 0; i < n; i++) {
            mClassId2Index.put(algoClasses[i].getClassId(), i);

//            log.info("map: class[" + i + "] = " + algoClasses[i].getClassId());
        }

        //// Map course with list of index of classes opened for that course in assignment plan
        //// based on courseId and classType
        HashMap<String, List<Integer>> mCourseIdCombineClassType2ClassIndex = new HashMap<>();
        for (int i = 0; i < n; i++) {
            String courseId = algoClasses[i].getCourseId();
            String classType = algoClasses[i].getClassType();
            String key = courseId + classType;

            mCourseIdCombineClassType2ClassIndex.computeIfAbsent(key, k -> new ArrayList<>());
            mCourseIdCombineClassType2ClassIndex.get(key).add(i);
        }

        //// Init
        int[][] priorityMatrix = new int[n][m]; // to be upgrade
        Arrays.stream(priorityMatrix).forEach(row -> Arrays.fill(row, Integer.MAX_VALUE));

        hourClass = new double[n];
        var mClassIdx2TeacherIndexes = new HashSet[n]; // mClassIdx2TeacherIndexes[i]: danh sach cac gv co the day lop i.
        for (int i = 0; i < n; i++) {
            mClassIdx2TeacherIndexes[i] = new HashSet<Integer>();
            hourClass[i] = algoClasses[i].getHourLoad();
        }

        maxHourTeacher = new double[m];
        for (int teacherIdx = 0; teacherIdx < m; teacherIdx++) {
            AlgoTeacherIM teacher = algoTeachers[teacherIdx];
            maxHourTeacher[teacherIdx] = teacher.getPrespecifiedHourLoad(); // xem ki cho nay

            List<Course4Teacher> courses = teacher.getCourses();
            if (null != courses) {
                for (Course4Teacher course : courses) {
                    int priority = course.getPriority();

                    String key = course.getId() + course.getClassType();
                    List<Integer> classIndexes = mCourseIdCombineClassType2ClassIndex.get(key);
                    if (null != classIndexes) {
                        for (int classIdx : classIndexes) {
                            //mClassIdx2TeacherIndexes[classIdx].add(new AlgoTeacherClassPriorityModel(teacherIdx,classIdx,priority));
                            mClassIdx2TeacherIndexes[classIdx].add(teacherIdx);
                            priorityMatrix[classIdx][teacherIdx] = priority;
                        }
                    }
                    // Testing and debugging only
//                    else {
//                        // This is normal case because TeacherCourseForAssignmentPlan may contain courses that not have in assignment plan
//                        // No problem
//                        log.info(name() + "::no class for course " + course.getCourseId());
//                    }
                }
            }
        }

        int[][] pa = null;
        if (null != preAssignments && preAssignments.length != 0) {
            log.info(thisMethodName + "prepare preAssignments with size = " + preAssignments.length);

            pa = new int[preAssignments.length][2];
            for (int i = 0; i < preAssignments.length; i++) {
                AlgoClassIM algoClass = preAssignments[i].getAlgoClassIM();
                AlgoTeacherIM algoTeacher = preAssignments[i].getAlgoTeacherIM();
                int classIndex = mClassId2Index.get(algoClass.getClassId());
                int teacherIndex = mTeacherId2Index.get(algoTeacher.getId());

                mClassIdx2TeacherIndexes[classIndex].clear();
                mClassIdx2TeacherIndexes[classIndex].add(teacherIndex);

                log.info(thisMethodName + "preAssign class[" + classIndex + "] = " + algoClass.getClassId()
                         + " - teacherIndex[" + teacherIndex + "] = " + algoTeacher.getId());

                //mClassIdx2TeacherIndexes[classIndex].add(new AlgoTeacherClassPriorityModel(teacherIndex,classIndex,1));

                pa[i][0] = classIndex;
                pa[i][1] = teacherIndex;
            }
        }

        //// Testing and debugging only
//        for (int i = 0; i < n; i++) {
//            System.out.println("Class " +
//                               algoClasses[i].getId() +
//                               " " +
//                               algoClasses[i].getCourseId() +
//                               "-" +
//                               algoClasses[i].getCourseName() +
//                               ": ");
//
//
//            for (int j : mClassIdx2TeacherIndexes[i]) {
//                if (algoTeachers[j].getId().equals("bang.banha@hust.edu.vn")) {
//                    System.out.println("teacher " + j + ": " + algoTeachers[j].getId());
//                    System.out.println("Class " +
//                                       algoClasses[i].getId() +
//                                       " " +
//                                       algoClasses[i].getCourseId() +
//                                       "-" +
//                                       algoClasses[i].getCourseName() +
//                                       ": ");
//
//                }
//            }
//        }

        boolean[][] conflict = new boolean[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                boolean isConflict = TimetableConflictChecker.conflictMultiTimeTable(
                    algoClasses[i].getTimetable(),
                    algoClasses[j].getTimetable());

                conflict[i][j] = isConflict;
                conflict[j][i] = isConflict;

                // Testing and debugging only
//                if (conflict[i][j]) {
//                    log.info("Conflict " + algoClasses[i].getTimetable() + " VS. " + algoClasses[j].getTimetable());
//                } else {
//                    log.info("NOT Conflict " + algoClasses[i].getTimetable() + " VS. " + algoClasses[j].getTimetable());
//                }
            }
        }

        //// Note: Not pay attention to the school week
        boolean[][] classDays = new boolean[n][7]; // Store the days of the week on which the class take place
        Arrays.stream(classDays).forEach(row -> Arrays.fill(row, false));

        for (int i = 0; i < n; i++) {
            for (int d : daysOfValidClass.get(i)) {
                classDays[i][d - 2] = true;
            }
        }

        MapDataInput mapDataInput = new MapDataInput(
            n,
            m,
            mClassIdx2TeacherIndexes,
            conflict,
            priorityMatrix,
            hourClass,
            maxHourTeacher,
            pa,
            classDays,
            teachersWantToMinimizeWorkingDays);

        //mapDataInput.savePlainTextFile("mClassIdx2TeacherIndexes:/tmp/data-bca/1.txt");
        //MaxLoadConstraintORToolMIPSolver mipSolver =
        //    new MaxLoadConstraintORToolMIPSolver(n, m, mClassIdx2TeacherIndexes, priorityMatrix, conflict, hourClass, maxHourTeacher);

        // Solve
        BaseSolver baseSolver = new BaseSolver();
        baseSolver.setInput(mapDataInput);
        boolean solved = baseSolver.solve(input.getConfig());

        int[] algoAssignments;
        if (solved) {
            algoAssignments = baseSolver.getAssignment();
            log.info(thisMethodName + "Found optimal solution!!");
            log.info(thisMethodName + "numNotAssignClasses = " + baseSolver.getNotAssignedClasses().size());
        } else {
            return null;
        }
        /*
        else {
            log.info("computeTeacherClassAssignment, MIP cannot find optimal solution, Apply CBLS");
            CBLSSolver solver = new CBLSSolver(n, m, mClassIdx2TeacherIndexes, priorityMatrix, conflict, hourClass, maxHourTeacher);
            solver.solve();
            algoAssignments = solver.getSolution();
        }
        */

        // Decoding
        ////
        HashMap<AlgoTeacherIM, List<AlgoClassIM>> mTeacher2AssignedClasses = new HashMap<>();
        TeacherClassAssignmentModel[] assignments = new TeacherClassAssignmentModel[algoClasses.length];
        for (int i = 0; i < algoClasses.length; i++) {
            AlgoClassIM algoClass = algoClasses[i];

            if (algoAssignments[i] > -1) {
                AlgoTeacherIM algoTeacher = algoTeachers[algoAssignments[i]];
                assignments[i] = new TeacherClassAssignmentModel(algoClass, algoTeacher);

                mTeacher2AssignedClasses.computeIfAbsent(algoTeacher, k -> new ArrayList<>());
                mTeacher2AssignedClasses.get(algoTeacher).add(algoClass);
            } else {
                notAssignedClasses.add(algoClass);
            }
        }

        ////
        ClassesAssigned2TeacherModel[] classesAssigned2Teacher = new ClassesAssigned2TeacherModel[algoTeachers.length];
        for (int i = 0; i < algoTeachers.length; i++) {
            classesAssigned2Teacher[i] = new ClassesAssigned2TeacherModel(
                algoTeachers[i],
                mTeacher2AssignedClasses.get(algoTeachers[i]));
        }

        //// Currently, do nothing with this
        int numEmpty = 0;
        for (int i = 0; i < n; i++) {
            if (mClassIdx2TeacherIndexes[i].size() == 0) {
                log.info(thisMethodName + "empty domain course "
//                         + algoClasses[i].getCourseId()
//                         + " - "
                         + algoClasses[i].getCourseName());
                numEmpty++;
            }
        }

        log.info(thisMethodName + "num empty domain = " + numEmpty);

        ////
        TeacherClassAssignmentOM teacherClassAssignmentOM = new TeacherClassAssignmentOM(
            assignments,
            classesAssigned2Teacher,
            notAssignedClasses);

        ////
        int[] numClassesOfTeacher = new int[m];
        Arrays.fill(numClassesOfTeacher, 0);

        for (int i = 0; i < n; i++) {
            if (algoAssignments[i] > -1) {
                numClassesOfTeacher[algoAssignments[i]]++;
            }
        }

        // Verify solution
        for (int t = 0; t < m; t++) {
            log.info(thisMethodName + "teacher[" + t + "] - " + algoTeachers[t].getId() +
                     " has " + numClassesOfTeacher[t]);

            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    if (algoAssignments[i] == t && algoAssignments[j] == t && conflict[i][j]) {
                        log.error(thisMethodName + "BUG with class " + i + " and " + j + " of teacher " + t + ": " +
                                  algoClasses[i].getTimetable() + " <-> " + algoClasses[j].getTimetable());
                    }
                }
            }
        }

        return teacherClassAssignmentOM;
    }
}
