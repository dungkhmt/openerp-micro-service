package openerp.openerpresourceserver.trainingprogcourse.algorithm;

import com.google.ortools.Loader;
import com.google.ortools.constraintsolver.*;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import com.google.ortools.sat.LinearExpr;
import openerp.openerpresourceserver.trainingprogcourse.enity.TrainingProgCourse;
import openerp.openerpresourceserver.trainingprogcourse.enity.TrainingProgPrerequisite;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CourseScheduler {
    public Map<Long, List<String>> scheduleCourses(List<TrainingProgCourse> courses, int semesterCount) {
        Loader.loadNativeLibraries();
        Solver solver = new Solver("Course Scheduling");

        int numCourses = courses.size();
        if (numCourses < semesterCount) {
            throw new IllegalArgumentException("The number of courses is less than the number of semesters.");
        }

        long[] credits = new long[numCourses];
        String[] courseIds = new String[numCourses];
        Map<String, Integer> courseIndex = new HashMap<>();

        // Lưu trữ thông tin tín chỉ và ID môn học
        for (int i = 0; i < numCourses; i++) {
            credits[i] = courses.get(i).getCredit();
            courseIds[i] = courses.get(i).getId();
            courseIndex.put(courseIds[i], i);
        }

        // Tạo biến quyết định cho việc phân bổ môn học vào các kỳ
        IntVar[] courseToSemester = new IntVar[numCourses];
        for (int i = 0; i < numCourses; i++) {
            courseToSemester[i] = solver.makeIntVar(0, semesterCount - 1, "semester_" + courseIds[i]);
        }

        // Thêm ràng buộc tiên quyết cho các môn học
        for (TrainingProgCourse course : courses) {
            int courseIndexA = courseIndex.get(course.getId());
            Set<TrainingProgPrerequisite> prerequisites = course.getPrerequisites();
            for (TrainingProgPrerequisite prerequisite : prerequisites) {
                int courseIndexB = courseIndex.get(prerequisite.getPrerequisiteCourse().getId());
                solver.addConstraint(solver.makeGreaterOrEqual(
                        courseToSemester[courseIndexA],
                        solver.makeSum(courseToSemester[courseIndexB], 1)));
            }
        }

        // Tính toán tổng tín chỉ cho mỗi kỳ học với mục tiêu cân bằng tốt hơn
        IntVar[] semesterTotalCredits = new IntVar[semesterCount];
        IntVar[][] semesterCourseMatrix = new IntVar[semesterCount][numCourses];

        // Lặp qua các kỳ học và tính tổng tín chỉ cho mỗi kỳ
        for (int semester = 0; semester < semesterCount; semester++) {
            IntVar[] semesterCredits = new IntVar[numCourses];
            for (int i = 0; i < numCourses; i++) {
                semesterCredits[i] = solver.makeIntVar(0, credits[i], "credits_" + i + "_" + semester);
                IntVar isCourseInSemester = solver.makeIsEqualCstVar(courseToSemester[i], semester);
                semesterCourseMatrix[semester][i] = isCourseInSemester;
                solver.addConstraint(solver.makeEquality(semesterCredits[i],
                        solver.makeProd(isCourseInSemester, credits[i])));
            }
            semesterTotalCredits[semester] = solver.makeSum(semesterCredits).var();
        }

        // Ràng buộc không có kỳ học trống
        for (int semester = 0; semester < semesterCount; semester++) {
            IntVar coursesInSemester = solver.makeSum(semesterCourseMatrix[semester]).var();
            solver.addConstraint(solver.makeGreaterOrEqual(coursesInSemester, 1)); // At least 1 course per semester
        }

        // Tối thiểu hóa sự chênh lệch tín chỉ giữa các kỳ học
        IntVar minTotalCredits = solver.makeMin(semesterTotalCredits).var();
        IntVar maxTotalCredits = solver.makeMax(semesterTotalCredits).var();
        IntVar creditDifference = solver.makeDifference(maxTotalCredits, minTotalCredits).var();

        // Các ràng buộc tín chỉ với độ lệch chặt chẽ hơn
        long totalCredits = Arrays.stream(credits).sum();
        long avgCreditsPerSemester = totalCredits / semesterCount;
        long maxDeviation = Math.max(3, avgCreditsPerSemester / 4); // More strict deviation limit

        for (IntVar semesterCredits : semesterTotalCredits) {
            solver.addConstraint(solver.makeLessOrEqual(semesterCredits, avgCreditsPerSemester + maxDeviation));
            solver.addConstraint(solver.makeGreaterOrEqual(semesterCredits, avgCreditsPerSemester - maxDeviation));
        }

        // Cải thiện phân bổ số môn học vào các kỳ bằng giới hạn max min học phần 1 kì
        int minCoursesPerSemester = Math.max(1, numCourses / (semesterCount * 2));
        int maxCoursesPerSemester = (numCourses + semesterCount - 1) / semesterCount + 1;

        for (int semester = 0; semester < semesterCount; semester++) {
            IntVar coursesCount = solver.makeSum(semesterCourseMatrix[semester]).var();
            solver.addConstraint(solver.makeGreaterOrEqual(coursesCount, minCoursesPerSemester));
            solver.addConstraint(solver.makeLessOrEqual(coursesCount, maxCoursesPerSemester));
        }

        // Tối ưu hóa đa mục tiêu
        OptimizeVar[] objectives = new OptimizeVar[] {
                solver.makeMinimize(creditDifference, 2), // Increased weight for credit balance
                solver.makeMinimize(solver.makeMax(courseToSemester).var(), 1)
        };

        // Chiến lược tìm kiếm cải thiện
        DecisionBuilder db = solver.makePhase(courseToSemester,
                Solver.CHOOSE_PATH,
                Solver.ASSIGN_MIN_VALUE);

        solver.newSearch(db, objectives);

        Map<Long, List<String>> bestSolution = null;
        int bestObjectiveValue = Integer.MAX_VALUE;

        while (solver.nextSolution()) {
            int currentObjectiveValue = (int) creditDifference.value();
            if (currentObjectiveValue < bestObjectiveValue) {
                bestObjectiveValue = currentObjectiveValue;
                bestSolution = new HashMap<>();
                for (int i = 0; i < numCourses; i++) {
                    long semester = courseToSemester[i].value();
                    bestSolution.computeIfAbsent(semester, k -> new ArrayList<>()).add(courseIds[i]);
                }
            }
        }

        solver.endSearch();
        return bestSolution != null ? bestSolution : new HashMap<>();
    }

    public static Map<String, Integer> moveCourse(
            String targetCourseId,
            int targetSemester,
            List<TrainingProgCourse> courses,
            Map<String, Integer> initialSemesters,
            int totalSemesters) {
        Loader.loadNativeLibraries();

        // Kiểm tra đầu vào
        if (!initialSemesters.containsKey(targetCourseId)) {
            throw new IllegalArgumentException("Không tìm thấy môn học cần di chuyển");
        }
        if (targetSemester < 1 || targetSemester > totalSemesters) {
            throw new IllegalArgumentException("Kỳ học không hợp lệ");
        }

        try {
            Solver solver = new Solver("CourseMovement");

            // Tạo biến học kỳ cho mỗi môn học
            Map<String, IntVar> semesterVars = new HashMap<>();
            for (String courseId : initialSemesters.keySet()) {
                semesterVars.put(courseId, solver.makeIntVar(1, totalSemesters, "semester_" + courseId));
            }

            // 1. Ràng buộc môn học mục tiêu phải ở kỳ mới
            solver.addConstraint(
                    solver.makeEquality(semesterVars.get(targetCourseId), targetSemester));

            // 2. Xử lý ràng buộc môn tiên quyết
            Map<String, List<String>> prerequisites = new HashMap<>();
            for (TrainingProgCourse course : courses) {
                prerequisites.put(course.getId(),
                        course.getPrerequisites().stream()
                                .map(p -> p.getPrerequisiteCourse().getId())
                                .collect(Collectors.toList()));
            }

            // Thêm ràng buộc tiên quyết
            for (Map.Entry<String, List<String>> entry : prerequisites.entrySet()) {
                String courseId = entry.getKey();
                for (String prereqId : entry.getValue()) {
                    if (semesterVars.containsKey(prereqId)) {
                        solver.addConstraint(
                                solver.makeLess(semesterVars.get(prereqId),
                                        semesterVars.get(courseId)));
                    }
                }
            }

            // 3. Ràng buộc mỗi kỳ phải có ít nhất 1 môn học
            for (int semester = 1; semester <= totalSemesters; semester++) {
                final int currentSemester = semester;
                List<IntVar> coursesInSemester = new ArrayList<>();

                for (Map.Entry<String, IntVar> entry : semesterVars.entrySet()) {
                    IntVar isInSemester = solver.makeBoolVar("in_semester_" + entry.getKey() + "_" + semester);
                    solver.addConstraint(
                            solver.makeEquality(isInSemester,
                                    solver.makeIsEqualCstVar(entry.getValue(), currentSemester)));
                    coursesInSemester.add(isInSemester);
                }

                solver.addConstraint(
                        solver.makeGreaterOrEqual(
                                solver.makeSum(coursesInSemester.toArray(new IntVar[0])), 1));
            }

            // 4. Tạo biến đánh dấu môn học di chuyển
            Map<String, IntVar> moveVars = new HashMap<>();
            for (Map.Entry<String, Integer> entry : initialSemesters.entrySet()) {
                String courseId = entry.getKey();
                int initialSemester = entry.getValue();
                IntVar moveVar = solver.makeBoolVar("move_" + courseId);
                moveVars.put(courseId, moveVar);

                solver.addConstraint(
                        solver.makeEquality(moveVar,
                                solver.makeIsDifferentCstVar(
                                        semesterVars.get(courseId),
                                        initialSemester)));
            }

            // 5. Tối thiểu hóa số môn học di chuyển
            IntVar[] moveVarsArray = moveVars.values().toArray(new IntVar[0]);
            IntVar totalMoves = solver.makeSum(moveVarsArray).var();
            OptimizeVar objective = solver.makeMinimize(totalMoves, 1);

            // 6. Tạo DecisionBuilder với chiến lược tối ưu
            IntVar[] allVars = Stream.concat(
                            semesterVars.values().stream(),
                            moveVars.values().stream())
                    .toArray(IntVar[]::new);

            DecisionBuilder db = solver.makePhase(
                    allVars,
                    Solver.CHOOSE_FIRST_UNBOUND,
                    Solver.ASSIGN_MIN_VALUE);

            // 7. Giải model
            solver.newSearch(db, objective);

            // Tìm giải pháp tối ưu
            Map<String, Integer> bestSolution = null;
            long bestMoves = Long.MAX_VALUE;

            while (solver.nextSolution()) {
                long currentMoves = totalMoves.value();
                if (currentMoves < bestMoves) {
                    bestMoves = currentMoves;
                    bestSolution = new HashMap<>();
                    for (Map.Entry<String, IntVar> entry : semesterVars.entrySet()) {
                        bestSolution.put(entry.getKey(), (int) entry.getValue().value());
                    }
                }
            }

            solver.endSearch();

            if (bestSolution != null) {
                return bestSolution;
            } else {
                throw new IllegalStateException("Không tìm thấy giải pháp khả thi");
            }

        } catch (Exception e) {
            throw new IllegalStateException("Lỗi khi di chuyển môn học: " + e.getMessage());
        }
    }
}