package com.hust.baseweb.applications.education.teacherclassassignment.service;

import com.hust.baseweb.applications.education.entity.EduCourse;
import com.hust.baseweb.applications.education.repo.EduCourseRepo;
import com.hust.baseweb.applications.education.teacherclassassignment.entity.*;
import com.hust.baseweb.applications.education.teacherclassassignment.entity.compositeid.TeacherCourseId;
import com.hust.baseweb.applications.education.teacherclassassignment.model.*;
import com.hust.baseweb.applications.education.teacherclassassignment.model.teachersuggestion.MoveClassToTeacher;
import com.hust.baseweb.applications.education.teacherclassassignment.model.teachersuggestion.SuggestedTeacherAndActionForClass;
import com.hust.baseweb.applications.education.teacherclassassignment.model.teachersuggestion.TeacherCandidate;
import com.hust.baseweb.applications.education.teacherclassassignment.repo.*;
import com.hust.baseweb.applications.education.teacherclassassignment.utils.TimeTableStartAndDuration;
import com.hust.baseweb.applications.education.teacherclassassignment.utils.TimetableConflictChecker;
import com.hust.baseweb.entity.UserLogin;
import com.hust.baseweb.repo.UserLoginRepo;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Log4j2
@AllArgsConstructor(onConstructor_ = @Autowired)
@Service
public class ClassTeacherAssignmentPlanServiceImpl implements ClassTeacherAssignmentPlanService {

    private UserLoginRepo userLoginRepo;

    private ClassTeacherAssignmentPlanRepo classTeacherAssignmentPlanRepo;

    private ClassTeacherAssignmentClassInfoRepo classInfoRepo;

    private TeacherCourseRepo teacherCourseRepo;

    private EduTeacherRepo eduTeacherRepo;

    private EduCourseRepo eduCourseRepo;

    private TeacherClassAssignmentSolutionRepo solutionRepo;

    private TeacherCourseForAssignmentPlanRepo teacherCourseForAssignmentPlanRepo;

    private TeacherForAssignmentPlanRepo teacherForAssignmentPlanRepo;

    private TeacherClassAssignmentAlgoService teacherClassAssignmentAlgoService;

    @Override
    public ClassTeacherAssignmentPlan create(UserLogin u, ClassTeacherAssignmentPlanCreateModel input) {
        ClassTeacherAssignmentPlan classTeacherAssignmentPlan = new ClassTeacherAssignmentPlan();
        classTeacherAssignmentPlan.setPlanName(input.getPlanName());
        classTeacherAssignmentPlan.setCreatedStamp(new Date());
        classTeacherAssignmentPlan.setCreatedBy(u.getUserLoginId());
        classTeacherAssignmentPlan = classTeacherAssignmentPlanRepo.save(classTeacherAssignmentPlan);

        return classTeacherAssignmentPlan;
    }

    @Override
    public List<ClassTeacherAssignmentPlan> findAll() {
        List<ClassTeacherAssignmentPlan> classTeacherAssignmentPlans = classTeacherAssignmentPlanRepo.findAll();
        return classTeacherAssignmentPlans;
    }

    /**
     * OK
     *
     * @param planId
     * @return
     */
    @Override
    public ClassTeacherAssignmentPlanDetailModel getClassTeacherAssignmentPlanDetail(UUID planId) {
        ClassTeacherAssignmentPlan plan = classTeacherAssignmentPlanRepo.findById(planId).orElse(null);
        ClassTeacherAssignmentPlanDetailModel planDetail = new ClassTeacherAssignmentPlanDetailModel();

        if (null != plan) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");

            planDetail.setPlanId(plan.getId());
            planDetail.setPlanName(plan.getPlanName());
            planDetail.setCreatedByUserLoginId(plan.getCreatedBy());
            planDetail.setCreatedDate(dateFormat.format(plan.getCreatedStamp()));
        }

        return planDetail;
    }

    /**
     * OK
     *
     * @param planId
     * @return
     */
    @Override
    public List<ClassInfoForAssignment2TeacherModel> findClassesInPlan(UUID planId) {
        List<ClassTeacherAssignmentClassInfo> classes = classInfoRepo.findAllByPlanId(planId);
        List<TeacherCourse> teacherCourses = teacherCourseRepo.findAll();
        List<TeacherCourseForAssignmentPlan> teacherCourseInPlan = teacherCourseForAssignmentPlanRepo.findAllByPlanId(
            planId);

        // Compute extra information
        HashMap<String, Set<String>> mClassId2TeacherId = new HashMap<>();
        HashMap<String, Set<String>> mClassId2TeacherIdInPlan = new HashMap<>();
        List<ClassInfoForAssignment2TeacherModel> result = new ArrayList<>();
        for (ClassTeacherAssignmentClassInfo classInfo : classes) {
            mClassId2TeacherId.computeIfAbsent(classInfo.getClassId(), k -> new HashSet<>());
            mClassId2TeacherIdInPlan.computeIfAbsent(classInfo.getClassId(), k -> new HashSet<>());
            ClassInfoForAssignment2TeacherModel clazz = new ClassInfoForAssignment2TeacherModel(classInfo);

            for (TeacherCourse tc : teacherCourses) {
                try {
                    if (tc.getCourseId().equalsIgnoreCase(classInfo.getCourseId()) &&
                        tc.getClassType().equalsIgnoreCase(classInfo.getClassType())) {
                        mClassId2TeacherId.get(classInfo.getClassId()).add(tc.getTeacherId());
                    }
                } catch (NullPointerException ignored) {
                }
            }
            clazz.setNumberPossibleTeachers(mClassId2TeacherId.get(classInfo.getClassId()).size());

            for (TeacherCourseForAssignmentPlan tc : teacherCourseInPlan) {
                try {
                    if (tc.getCourseId().equalsIgnoreCase(classInfo.getCourseId()) &&
                        tc.getClassType().equalsIgnoreCase(classInfo.getClassType())) {
                        mClassId2TeacherIdInPlan.get(classInfo.getClassId()).add(tc.getTeacherId());
                    }
                } catch (NullPointerException ignored) {
                }
            }
            clazz.setNumberPossibleTeachersInPlan(mClassId2TeacherIdInPlan.get(classInfo.getClassId()).size());
            result.add(clazz);
        }

        return result;
    }

    /**
     * Temporarily OK
     *
     * @param planId
     * @param file
     * @return
     */
    @Transactional
    @Override
    public boolean extractExcelAndStoreDB(UUID planId, MultipartFile file) {
        List<ClassTeacherAssignmentClassInfo> classes = new ArrayList<>();

        try (InputStream is = file.getInputStream()) {
            XSSFWorkbook wb = new XSSFWorkbook(is);
            XSSFSheet sheet = wb.getSheetAt(0);
            int lastRowNum = sheet.getLastRowNum();

            for (int i = 1; i <= lastRowNum; i++) {
                Row row = sheet.getRow(i);
                Cell c = row.getCell(1);

                String schoolName = c.getStringCellValue();
                c = row.getCell(0);
                String semesterId = c.getStringCellValue();
                c = row.getCell(2);
                String classId = String.valueOf(Double.valueOf(c.getNumericCellValue()).intValue());
                c = row.getCell(4);
                String courseId = c.getStringCellValue();
                c = row.getCell(5);
                String className = c.getStringCellValue();
                c = row.getCell(7);
                String creditInfo = c.getStringCellValue();
                c = row.getCell(8);
                String classNote = c.getStringCellValue();
                c = row.getCell(23);
                String program = c.getStringCellValue();
                c = row.getCell(22);
                String semesterType = c.getStringCellValue();
                c = row.getCell(18);
                int enrollment = Double.valueOf(c.getNumericCellValue()).intValue();
                c = row.getCell(19);
                int maxEnrollment = Double.valueOf(c.getNumericCellValue()).intValue();
                c = row.getCell(21);
                String classType = c.getStringCellValue();
                c = row.getCell(26);
                String timeTable = c.getStringCellValue();
                c = row.getCell(27);
                String lesson = c.getStringCellValue();
                c = row.getCell(28);
                String department = null == c ? null : c.getStringCellValue();
                c = row.getCell(29);
                double hourLoad = c.getNumericCellValue();

                //
                ClassTeacherAssignmentClassInfo cls = new ClassTeacherAssignmentClassInfo();
                cls.setClassId(classId);
                cls.setClassName(className);
                cls.setCourseId(courseId);
                cls.setNote(classNote);
                cls.setSemesterId(semesterId);
                cls.setSemesterType(semesterType);
                cls.setProgram(program);
                cls.setTimetable(timeTable);
                cls.setLesson(lesson);
                cls.setDepartmentId(department);
                cls.setEnrollment(enrollment);
                cls.setMaxEnrollment(maxEnrollment);
                cls.setClassType(classType);
                cls.setCredit(creditInfo);
                cls.setSchoolName(schoolName);
                cls.setPlanId(planId);
                cls.setHourLoad(hourLoad);
                cls.setCreatedStamp(new Date());
                classes.add(cls);

                log.info(classId + "\t" + courseId + "\t" + className + "\t" + timeTable);
            }

            solutionRepo.deleteAll();
            classInfoRepo.deleteAll();

            classInfoRepo.saveAll(classes);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public String addTeacher(EduTeacher teacher) {
        Optional<EduTeacher> t = eduTeacherRepo.findById(teacher.getId());
        if (t.isPresent()) {
            return "Email đã tồn tại";
        }

        if (teacher.getUserLoginId() != null && !teacher.getUserLoginId().isEmpty()) {
            Optional<EduTeacher> t2 = eduTeacherRepo.findByUserLoginId(teacher.getUserLoginId());
            if (t2.isPresent()) {
                return "Tài khoản đã liên kết với email khác";
            }

            Optional<UserLogin> user = userLoginRepo.findById(teacher.getUserLoginId());
            if (!user.isPresent()) {
                return "Tài khoản không tồn tại";
            }
        }

        eduTeacherRepo.save(teacher);
        return "OK";
    }

    /**
     * OK
     *
     * @return
     */
    @Override
    public List<EduTeacher> findAllTeachers() {
        List<EduTeacher> teachers = eduTeacherRepo.findAll();
        teachers.sort(Comparator.comparing(EduTeacher::getId));
        return teachers;
    }

    @Override
    public Page<EduTeacher> findAllTeachersByPage(String keyword, Pageable pageable) {
        return eduTeacherRepo.findAllContain(keyword, pageable);
    }

    /**
     * OK
     *
     * @param planId
     * @return
     */
    @Override
    public List<TeacherForAssignmentPlan> findAllTeacherByPlanId(UUID planId) {
        List<TeacherForAssignmentPlan> teachersInPlan = teacherForAssignmentPlanRepo.findAllByPlanId(planId);
        teachersInPlan.sort(Comparator.comparing(TeacherForAssignmentPlan::getTeacherId));
        return teachersInPlan;
    }

    /**
     * OK
     *
     * @param planId
     * @param addedTeachers
     * @return
     */
    @Transactional
    @Override
    public void addTeacherToAssignmentPlan(UUID planId, TeacherForAssignmentPlan[] addedTeachers) {
        List<EduTeacher> teachers = eduTeacherRepo.findAll();
        Map<String, EduTeacher> mTeacherId2Teacher = new HashMap<>();
        for (EduTeacher t : teachers) {
            mTeacherId2Teacher.put(t.getId(), t);
//            log.info("addTeacherToAssignmentPlan, put teacher " + t.getTeacherId() + " maxCredit " + t.getMaxCredit());
        }

        for (TeacherForAssignmentPlan addedTeacher : addedTeachers) {
            TeacherForAssignmentPlan teacherForPlan = teacherForAssignmentPlanRepo.findByTeacherIdAndPlanId(
                addedTeacher.getTeacherId(),
                planId);

            if (null == teacherForPlan) {
                teacherForPlan = new TeacherForAssignmentPlan();
                teacherForPlan.setTeacherId(addedTeacher.getTeacherId());
                teacherForPlan.setPlanId(planId);
//                log.info("addTeacherToAssignmentPlan, record not exist -> create new!");
//            }

                EduTeacher teacher = mTeacherId2Teacher.get(addedTeacher.getTeacherId());
                if (teacher != null) {
                    if (addedTeacher.getMaxHourLoad() == 0) {
                        // take info from DB
                        teacherForPlan.setMaxHourLoad(teacher.getMaxHourLoad());
//                    log.info("addTeacherToAssignmentPlan, input maxHourLoad = 0 -> take from DB = " +
//                             teacherForPlan.getMaxHourLoad() + " = " + teacher.getMaxCredit());
                    }

                    teacherForAssignmentPlanRepo.save(teacherForPlan);
//                log.info("addTeacherToAssignmentPlan, save teacher " + teacherForPlan.getTeacherId() +
//                         " plan " + teacherForPlan.getPlanId() + " maxCredit  " + teacherForPlan.getMaxHourLoad());
                } else {
                    log.info("addTeacherToAssignmentPlan, not found teacher " + addedTeacher.getTeacherId());
                }
            }
        }
    }

    /**
     * OK
     *
     * @param planId
     * @param teacherIds
     */
    @Transactional
    @Override
    public void removeTeacherFromAssignmentPlan(UUID planId, String[] teacherIds) {
//        log.info("removeTeacherFromAssignmentPlan");
        for (String id : teacherIds) {
            TeacherForAssignmentPlan teacherForPlan = teacherForAssignmentPlanRepo.findByTeacherIdAndPlanId(id, planId);

            if (teacherForPlan != null) {
                // Remove related items from TeacherCourseForAssignmentPlan
                teacherCourseForAssignmentPlanRepo.deleteByPlanIdAndTeacherId(planId, id);
                // Remove related items from TeacherClassAssignmentSolution
                solutionRepo.deleteAllByPlanIdAndTeacherId(planId, id);

                teacherForAssignmentPlanRepo.delete(teacherForPlan);
//                log.info("removeTeacherFromAssignmentPlan, delete (" + id + "," + planId + ")");
            }
        }
    }

    /**
     * OK
     *
     * @param planId
     * @param classIds
     */
    @Transactional
    @Override
    public void removeClassFromAssignmentPlan(UUID planId, String[] classIds) {
        for (String classId : classIds) {
            if (null != classId) {
                classInfoRepo.deleteByPlanIdAndClassId(planId, classId);
                solutionRepo.deleteAllByPlanIdAndClassId(planId, classId);
//                log.info("removeClassFromAssignmentPlan, remove classId " + classId + " OK");
            }
        }
    }

    @Override
    public List<TeacherCourse> findAllTeacherCourse() {
        List<TeacherCourse> tc = teacherCourseRepo.findAll();
        tc.sort((tc1, tc2) -> {
            String key1 = tc1.getTeacherId() + tc1.getCourseId() + tc1.getClassType();
            String key2 = tc2.getTeacherId() + tc2.getCourseId() + tc2.getClassType();
            return key1.compareTo(key2);
        });
        return tc;
    }

    @Override
    public List<TeacherCourseForAssignmentPlan> findTeacherCourseOfPlan(UUID planId) {
        List<TeacherCourseForAssignmentPlan> tc = teacherCourseForAssignmentPlanRepo.findAllByPlanId(planId);
        tc.sort((tc1, tc2) -> {
            String key1 = tc1.getTeacherId() + tc1.getCourseId() + tc1.getClassType();
            String key2 = tc2.getTeacherId() + tc2.getCourseId() + tc2.getClassType();
            return key1.compareTo(key2);
        });
        return tc;
    }

    /**
     * Temporarily OK
     *
     * @param planId
     * @param choice
     * @param file
     * @return
     */
    @Transactional
    @Override
    public boolean extractExcelAndStoreDBTeacherCourse(UUID planId, String choice, MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            XSSFWorkbook wb = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = wb.getSheetAt(1);

            List<TeacherCourse> teacherCourses = new ArrayList<>();
            List<EduTeacher> teachers = new ArrayList<>();
            List<EduCourse> courses = new ArrayList<>();
            HashMap<String, TeacherCourse> mTeacherCourseId2TeacherCourse = new HashMap<>();
            HashSet<String> teacherIds = new HashSet<>();
            HashSet<String> courseIds = new HashSet<>();

            int sz = sheet.getLastRowNum();
            for (int i = 1; i <= sz; i++) {
                Row row = sheet.getRow(i);
                Cell c = row.getCell(4);
                String courseId = c.getStringCellValue();

                c = row.getCell(5);
                String courseName = c.getStringCellValue();

                c = row.getCell(25);
                String teacherName = c.getStringCellValue();

                c = row.getCell(26);
                String teacherId = c.getStringCellValue();

//                c = row.getCell(5);
                //int maxCredits = Integer.valueOf(c.getStringCellValue());

//                c = row.getCell(6);
//                int priority = 0;
//                if (c.getCellType().equals(CellType.STRING)) {
//                    //System.out.println("line i = " + i + ", column 6, str = " + c.getStringCellValue());
//                    try {
//                        priority = Integer.parseInt(c.getStringCellValue());
//                    } catch (Exception e) {
//                        System.out.println("exception priority convert str to int");
//                        //e.printStackTrace();
//                    }
//                } else if (c.getCellType().equals(CellType.NUMERIC)) {
//                    priority = (int) c.getNumericCellValue();
//                }

                c = row.getCell(21);
                String classType = c.getStringCellValue();
                c = row.getCell(7);
                String credit = "0";
                if (null != c && null != c.getStringCellValue() && !"".equals(c.getStringCellValue().trim())) {
                    credit = c.getStringCellValue().substring(0, 1);
                }

                TeacherCourse teacherCourse = new TeacherCourse();
                teacherCourse.setCourseId(courseId);
                teacherCourse.setTeacherId(teacherId);
                teacherCourse.setClassType(classType);
//                teacherCourse.setPriority(0);
                teacherCourse.setCreatedStamp(new Date());

//                TeacherCourseId id = new TeacherCourseId(teacherId, courseId, classType);
                String id = teacherId + courseId + classType;

                if (!teacherIds.contains(teacherId)) {
                    EduTeacher teacher = new EduTeacher();
                    teacher.setId(teacherId);
                    teacher.setTeacherName(teacherName);
                    teacher.setMaxHourLoad(30);
                    teacher.setCreatedStamp(new Date());

                    teachers.add(teacher);
                    teacherIds.add(teacherId);
                }

                if (!courseIds.contains(courseId)) { // OK
                    EduCourse course = new EduCourse();
                    course.setId(courseId);
                    course.setName(courseName);
                    course.setCredit(Short.parseShort(credit));
                    course.setCreatedStamp(new Date());

                    courses.add(course);
                    courseIds.add(courseId);
                }

                if (mTeacherCourseId2TeacherCourse.get(id) == null) {
                    mTeacherCourseId2TeacherCourse.put(id, teacherCourse);
                    teacherCourses.add(teacherCourse);
                    //log.info("extractExcelAndStoreDBTeacherCourse, add new " + courseId + ", " + teacherId + ", " + priority);
                } else {
                    log.info("extractExcelAndStoreDBTeacherCourse, " + courseId + ", " + teacherId + ", " +
//                             priority +
                             " EXISTS");
                }

                //teacherCourse  = teacherCourseRepo.save(teacherCourse);
                //log.info("extractExcelAndStoreDBTeacherCourse, " + courseId + ", " + teacherId + ", " + priority);
            }

            log.info("extractExcelAndStoreDBTeacherCourse, choice = " + choice + " courses.sz = " + courses.size()
                     + " teachers.sz = " + teachers.size() + ", teacher-course.sz = " + teacherCourses.size());


//            if (choice.equals("UPLOAD_COURSE")) {
            // check and store courses
            for (EduCourse c : courses) {
                EduCourse eduCourse = eduCourseRepo.findById(c.getId()).orElse(null);

                if (eduCourse != null) {
                    // EXISTS, do nothing
                    log.info("extractExcelAndStoreDBTeacherCourse, course " + c.getId() + " EXISTS");
                } else {
                    c = eduCourseRepo.save(c);
                    log.info("extractExcelAndStoreDBTeacherCourse, course " + c.getId() + " -> INSERTED");
                }
            }
//            } else if (choice.equals("UPLOAD_TEACHER")) {
            // check and store teachers
            for (EduTeacher t : teachers) {
                EduTeacher teacher = eduTeacherRepo.findById(t.getId()).orElse(null);

                if (teacher != null) {
                    // EXISTS do nothing
                    log.info("extractExcelAndStoreDBTeacherCourse, teacher " + t.getId() + " EXISTS");
                } else {
                    t = eduTeacherRepo.save(t);
                    log.info("extractExcelAndStoreDBTeacherCourse, teacher " + t.getId() + " INSERTED");
                }
            }
//            } else if (choice.equals("UPLOAD_TEACHER_COURSE")) {
            log.info("extractExcelAndStoreDBTeacherCourse, saveAll teacher-course");
            for (TeacherCourse tc : teacherCourses) {
                TeacherCourse existedTeacherCourse = teacherCourseRepo
                    .findById(new TeacherCourseId(tc.getTeacherId(), tc.getCourseId(), tc.getClassType()))
                    .orElse(null);

                if (null == existedTeacherCourse) {
                    teacherCourseRepo.save(tc);
                }
            }
//            teacherCourseRepo.saveAll(teacherCourses);
//            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * OK. Consider only TeacherCourseForAssignmentPlan that have teacher in plan
     *
     * @param im
     * @return
     */
    @Transactional
    @Override
    public boolean autoAssignTeacher2Class(RunAutoAssignTeacher2ClassInputModel im) {
        UUID planId = im.getPlanId();
        List<ClassTeacherAssignmentClassInfo> classes = classInfoRepo.findAllByPlanId(planId);

        log.info("autoAssignTeacher2Class, classes.sz = " + classes.size());

        // Preprocessing
        List<EduTeacher> allTeachers = eduTeacherRepo.findAll();
        HashMap<String, EduTeacher> mTeacherId2Teacher = new HashMap<>();
        for (EduTeacher t : allTeachers) {
            mTeacherId2Teacher.put(t.getId(), t);
        }

        ////
        List<EduCourse> allCourses = eduCourseRepo.findAll();
        HashMap<String, EduCourse> mCourseId2Course = new HashMap<>();
        for (EduCourse course : allCourses) {
            mCourseId2Course.put(course.getId(), course);
        }

        //// Todo: Xem xet Course4Teacher, thay bang TeacherCourseForAssignmentPlan
        List<TeacherCourseForAssignmentPlan> teacherCoursesInPlan = teacherCourseForAssignmentPlanRepo.findAllByPlanId(
            planId);
        ////// Build map for all teacher-course in plan even if the teacher is not in plan
        HashMap<String, List<Course4Teacher>> mTeacherId2CoursesInPlan = new HashMap<>();
        for (TeacherCourseForAssignmentPlan tc : teacherCoursesInPlan) {
            mTeacherId2CoursesInPlan.computeIfAbsent(tc.getTeacherId(), k -> new ArrayList<>()); // init

            EduCourse course = mCourseId2Course.get(tc.getCourseId());
            if (null != course) { // Todo: reconsider this logic
                Course4Teacher c4t = new Course4Teacher(
                    course.getId(),
                    course.getName(),
                    tc.getPriority(),
                    tc.getClassType());

                mTeacherId2CoursesInPlan.get(tc.getTeacherId()).add(c4t);
            }
        }

        // Encode data.
        List<TeacherForAssignmentPlan> teachersInPlan = teacherForAssignmentPlanRepo.findAllByPlanId(planId);
        AlgoClassIM[] algoClasses = new AlgoClassIM[classes.size()];

        //// TODO: reduces size of: mClassId2AlgoClass, mTeacherId2AlgoTeacher
        HashMap<String, AlgoClassIM> mClassId2AlgoClass = new HashMap<>();
        for (int i = 0; i < classes.size(); i++) {
            ClassTeacherAssignmentClassInfo c = classes.get(i);

            EduCourse course = mCourseId2Course.get(c.getCourseId());
            String courseName = null == course ? c.getCourseId() : course.getName();

            AlgoClassIM algoClass = new AlgoClassIM(
                c.getClassId(),
                c.getClassType(),
                c.getCourseId(),
                courseName,
                c.getLesson(),
                c.getHourLoad(), // TOBE UPGRADED
                false); // Will be updated value when processing preassigned solutions/assignments

            mClassId2AlgoClass.put(c.getClassId(), algoClass);
            algoClasses[i] = algoClass;
        }

        ////
        AlgoTeacherIM[] algoTeachers = new AlgoTeacherIM[teachersInPlan.size()];
        HashMap<String, AlgoTeacherIM> mTeacherId2AlgoTeacher = new HashMap<>();
        for (int i = 0; i < teachersInPlan.size(); i++) {
            TeacherForAssignmentPlan teacher = teachersInPlan.get(i);
            String teacherName = mTeacherId2Teacher.get(teacher.getTeacherId()).getTeacherName();

            AlgoTeacherIM algoTeacher = new AlgoTeacherIM(
                teacher.getTeacherId(),
                teacherName,
                mTeacherId2CoursesInPlan.get(teacher.getTeacherId()), // Consider only teacher in plan
                teacher.getMaxHourLoad(),// TOBE UPGRADED, tai sao lai dung maxHourLoad?
                "Y".equalsIgnoreCase(teacher.getMinimizeNumberWorkingDays()));

            mTeacherId2AlgoTeacher.put(teacher.getTeacherId(), algoTeacher);
            algoTeachers[i] = algoTeacher;
        }

        //// Load existing solution/assignment
        List<TeacherClassAssignmentSolution> preAssignedSolutions = solutionRepo.findAllByPinned(true);
        TeacherClassAssignmentModel[] preAssignments = new TeacherClassAssignmentModel[preAssignedSolutions.size()];

        for (int i = 0; i < preAssignedSolutions.size(); i++) {
            TeacherClassAssignmentSolution assignment = preAssignedSolutions.get(i);
            AlgoClassIM algoClass = mClassId2AlgoClass.get(assignment.getClassId());
            AlgoTeacherIM algoTeacher = mTeacherId2AlgoTeacher.get(assignment.getTeacherId());

            algoClass.setPinned(true);
            preAssignments[i] = new TeacherClassAssignmentModel(algoClass, algoTeacher);
        }

        AlgoTeacherAssignmentIM input = new AlgoTeacherAssignmentIM(
            algoTeachers,
            algoClasses,
            preAssignments,
            im.getConfig());

        // Testing and debugging only: save input json to file
//        Gson gson = new Gson();
//        String jsonInput = gson.toJson(input);
//        try {
//            PrintWriter out = new PrintWriter("teachingAssignment.json");
//            out.write(jsonInput);
//            out.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        // Solve
        TeacherClassAssignmentOM om = teacherClassAssignmentAlgoService.computeTeacherClassAssignment(input);
        if (null == om) {
            return false;
        }

        // Post-processing
        //// Remove all existing solution items of planId
        solutionRepo.deleteAllByPlanId(planId);

        //// Store solution to DB
        log.info("autoAssignTeacher2Class, START storing solution to DB");

        TeacherClassAssignmentModel[] assignments = om.getAssignments();
        for (int i = 0; i < assignments.length; i++) {
            TeacherClassAssignmentModel assignment = assignments[i];

            if (null == assignment) {
                log.info("autoAssignTeacher2Class, found assignment " + i + " NULL, class is not assigned");
                continue;
            }

            AlgoClassIM algoClass = assignment.getAlgoClassIM();
            if (null == algoClass) {
                log.info("autoAssignTeacher2Class, found assignment " + i + " with class NULL");
                continue;
            }

            log.info("autoAssignTeacher2Class, assign class " + algoClass.getClassId() +
                     " of course " + algoClass.getCourseId() +
                     " to teacher " + assignment.getAlgoTeacherIM().getId());

            TeacherClassAssignmentSolution solution = new TeacherClassAssignmentSolution();

            solution.setClassId(algoClass.getClassId());
            solution.setPlanId(planId);
            solution.setTeacherId(assignment.getAlgoTeacherIM().getId());
            solution.setPinned(algoClass.isPinned());
            solution.setCreatedStamp(new Date());

            solutionRepo.save(solution);
//            log.info("autoAssignTeacher2Class, save solution " + solution.getClassId() + " --> " + solution.getTeacherId());
        }

        // TODO: why return false?
        return false;
    }

    /**
     * OK
     *
     * @param planId
     * @return
     */
    @Override
    public List<ClassTeacherAssignmentSolutionModel> getNotAssignedClassSolution(UUID planId) {
        List<EduCourse> allCourses = eduCourseRepo.findAll();
        HashMap<String, EduCourse> mCourseId2Course = new HashMap<>();
        for (EduCourse c : allCourses) {
            mCourseId2Course.put(c.getId(), c);
        }

        List<TeacherClassAssignmentSolution> assignments = solutionRepo.findAllByPlanId(planId);
        HashSet<String> assignedClassIds = new HashSet<>();
        for (TeacherClassAssignmentSolution s : assignments) {
            assignedClassIds.add(s.getClassId());
        }

        List<ClassTeacherAssignmentClassInfo> allClasses = classInfoRepo.findAllByPlanId(planId);
        List<ClassTeacherAssignmentSolutionModel> notAssignedClasses = new ArrayList<>();
        for (ClassTeacherAssignmentClassInfo c : allClasses) {
            if (!assignedClassIds.contains(c.getClassId())) {
                ClassTeacherAssignmentSolutionModel model = new ClassTeacherAssignmentSolutionModel();

                model.setClassCode(c.getClassId());
                model.setClassType(c.getClassType());
                model.setCourseId(c.getCourseId());
                model.setTimetable(c.getLesson());

                EduCourse course = mCourseId2Course.get(c.getCourseId());
                if (null != course) {
                    model.setCourseName(course.getName());
                } else {
                    log.info("getNotAssignedClassSolution, courseId = " + c.getCourseId() + " has not name???");
                }

                notAssignedClasses.add(model);
            }
        }

        return notAssignedClasses;
    }

    /**
     * Don't use this logic anymore, consider removing in future
     *
     * @param classId
     * @param planId
     * @return
     */
//    @Override
//    public List<SuggestedTeacherForClass> getSuggestedTeacherForClass(String classId, UUID planId) {
//        List<ClassTeacherAssignmentClassInfo> classes = classInfoRepo.findByClassId(classId);
//        String courseId = null;
//        String timetable = "";
//        List<TeacherForAssignmentPlan> teachersInPlan = teacherForAssignmentPlanRepo.findAllByPlanId(planId);
//
//        for (ClassTeacherAssignmentClassInfo c : classes) {
//            courseId = c.getCourseId();
//            timetable = c.getLesson();
//            break;
//        }
//        List<SuggestedTeacherForClass> lst = new ArrayList<>();
//
//        log.info("getSuggestedTeacherForClass, courseId = " + courseId + ", planId = " + planId);
//        // TOBE improved
//        //List<TeacherCourse> teacherCourses = teacherCourseRepo.findAll();
//        List<TeacherCourseForAssignmentPlan> teacherCourses = teacherCourseForAssignmentPlanRepo.findAllByPlanId(planId);// consider only item in the plan
//
//        //for (TeacherCourse tc : teacherCourses) {
//        for (TeacherCourseForAssignmentPlan tc : teacherCourses) {
//            if (tc.getCourseId().equals(courseId)) {
//                SuggestedTeacherForClass t = new SuggestedTeacherForClass();
//                t.setTeacherId(tc.getTeacherId());
//                t.setTeacherName(tc.getTeacherId());
//                //t.setHourLoad(0.0);// TOBE upgrade
//
//                StringBuilder info = new StringBuilder(tc.getTeacherId() + ": ");
//
//                // get list of classes assigned to teacherId for more detail about suggestion
//                List<TeacherClassAssignmentSolution> assignments =
//                    solutionRepo.findAllByPlanIdAndTeacherId(planId, tc.getTeacherId());
//
//                double hourLoadOfTeacher = 0;
//
//                for (TeacherClassAssignmentSolution tcs : assignments) {
//                    List<ClassTeacherAssignmentClassInfo> ctai = classInfoRepo.findByClassId(tcs.getClassId());
//                    ClassTeacherAssignmentClassInfo c;
//                    if (ctai != null && ctai.size() > 0) {
//                        c = ctai.get(0);
//                    } else {
//                        continue;
//                    }
//                    hourLoadOfTeacher += c.getHourLoad();
//                    boolean conflict = TimetableConflictChecker.conflict(c.getLesson(), timetable);
//                    if (conflict) {
//                        info
//                            .append(" [Conflict: Class ")
//                            .append(c.getClassId())
//                            .append(", TimeTable ")
//                            .append(c.getLesson())
//                            .append("] ");
//                    }
//                }
//                info.append(" hour = ").append(hourLoadOfTeacher);
//                t.setInfo(info.toString());
//                lst.add(t);
//            }
//        }
//
//        return lst;
//    }

    /**
     * OK
     *
     * @return
     */
    private boolean checkCanAssign(
        ClassTeacherAssignmentClassInfo selectedClass,
        ClassTeacherAssignmentClassInfo c,
        TeacherCourseForAssignmentPlan tc2,
        HashMap<String, List<TeacherClassAssignmentSolution>> mTeacherToAssignedClass,
        HashMap<String, ClassTeacherAssignmentClassInfo> mClassIdToClassInfo,
        HashMap<String, TeacherClassAssignmentSolution> mClassIdToAssignedTeacher
    ) {
//        String t1 = mClassIdToAssignedTeacher.get(selectedClass.getClassId()).getTeacherId();

        // t1: current teacher assigned to the selectedClass
        // selectedClass and c are conflicting classes
        // CHECK if the selectedClass can be removed from t1, and assigned to teacher tc1 (of c)
        // then c is removed from tc1 and reassigned to teacher tc2

        List<TeacherClassAssignmentSolution> assignedClassesOfTeacher2 = mTeacherToAssignedClass.get(tc2.getTeacherId());
        if (null != assignedClassesOfTeacher2) {
            for (TeacherClassAssignmentSolution s : assignedClassesOfTeacher2) {
                ClassTeacherAssignmentClassInfo ci = mClassIdToClassInfo.get(s.getClassId());
                if (ci != selectedClass) {
                    if (TimetableConflictChecker.conflictMultiTimeTable(ci.getLesson(), c.getLesson())) {
                        return false;
                    }
                } else {
                    // IGNORE this case: tc2 is equal t1 which is the current teacher assign to selectedClass
                    // selectedClass will be removed from t1 (which is tc2), thus ci will no longer to be in tc2
                    // -> no need to check conflict
                }
            }
        }

        return true;
    }

    /**
     * Temporarily OK.
     * Include classType.
     * conflictClass of tc2 may be pinned but don't care, it can still move to tc3
     * Upgrade after defending
     *
     * @param classId
     * @param planId
     * @return
     */
    @Override
    public List<SuggestedTeacherAndActionForClass> getSuggestedTeacherAndActionForClass(String classId, UUID planId) {
        List<TeacherForAssignmentPlan> teachersInPlan = teacherForAssignmentPlanRepo.findAllByPlanId(planId);
        HashSet<String> teacherInPlanIds = new HashSet<>();
        for (TeacherForAssignmentPlan t : teachersInPlan) {
            teacherInPlanIds.add(t.getTeacherId());
        }

        //
        List<ClassTeacherAssignmentClassInfo> classes = classInfoRepo.findAllByPlanId(planId);
        HashMap<String, ClassTeacherAssignmentClassInfo> mClassId2Class = new HashMap<>();
        for (ClassTeacherAssignmentClassInfo c : classes) {
            mClassId2Class.put(c.getClassId(), c);
        }

        List<SuggestedTeacherAndActionForClass> suggestedTeachers = new ArrayList<>();

//        log.info("getSuggestedTeacherAndActionForClass, courseId = " + courseId + ", planId = " + planId);
        // TOBE improved
        //List<TeacherCourse> teacherCourses = teacherCourseRepo.findAll();
        List<TeacherClassAssignmentSolution> assignments = solutionRepo.findAllByPlanId(planId);
        HashMap<String, List<TeacherClassAssignmentSolution>> mTeacherToAssignedClass = new HashMap<>();
        HashMap<String, TeacherClassAssignmentSolution> mClassIdToAssignedTeacher = new HashMap<>();
        for (TeacherClassAssignmentSolution assignment : assignments) {
            mTeacherToAssignedClass.computeIfAbsent(assignment.getTeacherId(), k -> new ArrayList<>());

            mTeacherToAssignedClass.get(assignment.getTeacherId()).add(assignment);
            mClassIdToAssignedTeacher.put(assignment.getClassId(), assignment);
        }

        ClassTeacherAssignmentClassInfo selectedClass = classInfoRepo.findByClassIdAndPlanId(classId, planId);
        TeacherClassAssignmentSolution assignment = mClassIdToAssignedTeacher.get(selectedClass.getClassId());
        String assignedTeacherId = null == assignment ? null : assignment.getTeacherId();

        // Explore possibilities for new teachers
        // Consider only item in the plan
        for (TeacherCourseForAssignmentPlan tc1 :
            teacherCourseForAssignmentPlanRepo.findAllByPlanIdAndCourseIdAndClassType(
                planId,
                selectedClass.getCourseId(),
                selectedClass.getClassType())) {

            if (!tc1.getTeacherId().equalsIgnoreCase(assignedTeacherId) &&
                teacherInPlanIds.contains(tc1.getTeacherId())) {

                log.info("getSuggestedTeacherAndActionForClass, consider teacher " +
                         tc1.getTeacherId() + " for class " + classId);

                List<TeacherClassAssignmentSolution> assignedClasses = mTeacherToAssignedClass.get(tc1.getTeacherId());
                List<ClassTeacherAssignmentClassInfo> conflictClasses = new ArrayList<>(); // can be empty
                if (null != assignedClasses) {
                    log.info("getSuggestedTeacherAndActionForClass, consider teacher " + tc1.getTeacherId()
                             + " HAS " + assignedClasses.size() + " assigned classes");

                    // Collect all classes in assignedClasses that conflict with selectedClass
                    for (TeacherClassAssignmentSolution s : assignedClasses) {
                        ClassTeacherAssignmentClassInfo c = mClassId2Class.get(s.getClassId());

                        if (TimetableConflictChecker.conflictMultiTimeTable(selectedClass.getLesson(), c.getLesson())) {
                            conflictClasses.add(c);
                            log.info("getSuggestedTeacherAndActionForClass, consider teacher " + tc1.getTeacherId() +
                                     " for class " + classId +
                                     " FOUND conflict class " + c.getClassId() + " with timetable " + c.getLesson());
                        }
                    }
                }

                // Voi moi lop cua tc1 bi xung dot --> co gang tim tc2 nhan lop do de gan selectedClass cho tc1
                // tc2 cung co the la gv t hien tai cua selectedClass, khi do se thanh doi lop giua t va tc1
                // Find other teachers for each conflicting class in conflictClasses with selectedClass
                boolean teacherOk = true;
                HashMap<String, List<TeacherCourseForAssignmentPlan>> mClassId2NewTeacher = new HashMap<>();
                for (ClassTeacherAssignmentClassInfo conflictClass : conflictClasses) {
                    // Consider a conflicting class conflictClass of tc1, try to find other teachers tc2 for conflictClass
                    for (TeacherCourseForAssignmentPlan tc2 :
                        teacherCourseForAssignmentPlanRepo.findAllByPlanIdAndCourseIdAndClassType(
                            planId,
                            conflictClass.getCourseId(),
                            conflictClass.getClassType())) {

                        if (!tc2.getTeacherId().equalsIgnoreCase(tc1.getTeacherId()) &&
                            teacherInPlanIds.contains(tc2.getTeacherId())
                        ) {
                            // Gan selectedClass cho tc1 va gan conflictClass cua tc1 cho tc2
                            if (checkCanAssign(
                                selectedClass,
                                conflictClass,
                                tc2,
                                mTeacherToAssignedClass,
                                mClassId2Class,
                                mClassIdToAssignedTeacher)
                            ) {
                                mClassId2NewTeacher.computeIfAbsent(conflictClass.getClassId(), k -> new ArrayList<>());
                                mClassId2NewTeacher.get(conflictClass.getClassId()).add(tc2);
                            }
                        }

                        // Only consider tc2 different from assignedTeacherId, this logic can be ignored
//                        if (!tc2.getTeacherId().equals(assignedTeacherId) &&
//                            !tc2.getTeacherId().equals(tc1.getTeacherId()) &&
//                            conflictClass.getCourseId().equals(tc2.getCourseId()) &&
//                            teacherInPlanIds.contains(tc2.getTeacherId())
//                        ) {
//                            boolean ok = true;
//                            List<TeacherClassAssignmentSolution> Li = mTeacherToAssignedClass.get(tc2.getTeacherId());
//                            if (Li != null) {
//                                for (TeacherClassAssignmentSolution si : Li) {
//                                    ClassTeacherAssignmentClassInfo ci = mClassId2Class.get(si.getClassId());
//                                    if (TimetableConflictChecker.conflictMultiTimeTable(
//                                        ci.getLesson(),
//                                        conflictClass.getLesson())) {
//                                        ok = false;
//                                        break;
//                                    }
//                                }
//                            }
//                            if (ok) {
//                                mClassId2NewTeacher.computeIfAbsent(conflictClass.getClassId(), k -> new ArrayList<>());
//                                mClassId2NewTeacher.get(conflictClass.getClassId()).add(tc2);
//                            }
//                        }
                    }

                    if (mClassId2NewTeacher.get(conflictClass.getClassId()) == null) {
                        teacherOk = false;
                        break;
                    }
                }

                if (teacherOk) {
                    SuggestedTeacherAndActionForClass suggestedTeacher = new SuggestedTeacherAndActionForClass();
                    suggestedTeacher.setTeacherId(tc1.getTeacherId());
                    suggestedTeacher.setTeacherName(tc1.getTeacherId());
                    //suggestedTeacher.setHourLoad(0.0);// TOBE upgrade

                    List<MoveClassToTeacher> movedClasses = new ArrayList<>();
                    for (ClassTeacherAssignmentClassInfo conflictClass : conflictClasses) {
                        List<TeacherCourseForAssignmentPlan> newTeachers = mClassId2NewTeacher.get(conflictClass.getClassId());
                        List<TeacherCandidate> teachers = new ArrayList<>();
                        StringBuilder infoNewTeachers = new StringBuilder();

                        if (null != newTeachers) {
                            for (TeacherCourseForAssignmentPlan tc : newTeachers) {
                                TeacherCandidate candidate = new TeacherCandidate();
                                candidate.setTeacherId(tc.getTeacherId());
                                teachers.add(candidate);
                                infoNewTeachers.append(tc.getTeacherId()).append("\n");
                            }
                        } else {
                            log.info("getSuggestedTeacherAndActionForClass, class " +
                                     conflictClass.getClassId() + " DOES NOT has new teachers");
                        }

                        MoveClassToTeacher movedClass = new MoveClassToTeacher();
                        movedClass.setClassCode(conflictClass.getClassId());
                        movedClass.setTeachers(teachers);
                        movedClass.setInfoNewTeachers(infoNewTeachers.toString());
                        movedClasses.add(movedClass);
                    }

                    suggestedTeacher.setMoveClass(movedClasses);
                    suggestedTeachers.add(suggestedTeacher);
                }
            }
        }

        suggestedTeachers.sort(Comparator.comparing(SuggestedTeacherAndActionForClass::getTeacherId));
        return suggestedTeachers;
    }

    /**
     * OK
     *
     * @param planId
     * @return
     */
    // Todo: optimize
    @Override
    public List<ClassesAssignedToATeacherModel> getClassesAssignedToATeacherSolutionDuplicateWhenMultipleFragmentTimeTable(
        UUID planId
    ) {
        List<ClassesAssignedToATeacherModel> gridViewData = new ArrayList<>();
        List<TeacherForAssignmentPlan> teachersInPlan = teacherForAssignmentPlanRepo.findAllByPlanId(planId);

        //
        List<EduTeacher> allTeachers = eduTeacherRepo.findAll();
        HashMap<String, EduTeacher> mId2Teacher = new HashMap<>();
        for (EduTeacher t : allTeachers) {
            mId2Teacher.put(t.getId(), t);
        }

        //
        HashSet<String> teacherHasClasses = new HashSet<>();
        HashMap<String, List<ClassTeacherAssignmentSolutionModel>> mTeacherId2AssignedClasses = new HashMap<>();
        List<ClassTeacherAssignmentSolutionModel> solutionModels = getClassTeacherAssignmentSolution(planId);
        for (ClassTeacherAssignmentSolutionModel s : solutionModels) {
            teacherHasClasses.add(s.getTeacherId());

            // if the class s has two or more fragments, then duplicate multiple fragments
            mTeacherId2AssignedClasses.computeIfAbsent(s.getTeacherId(), k -> new ArrayList<>());
            if (s.isMultipleFragments()) {
                ClassTeacherAssignmentSolutionModel[] fragments = s.checkMultipleFragmentsAndDuplicate();

//                log.info("getClassesAssignedToATeacherSolutionDuplicateWhenMultipleFragmentTimeTable, class " +
//                         s.getClassCode() + " has multiple timetable fragments.length = " + fragments.length);

                for (ClassTeacherAssignmentSolutionModel fragment : fragments) {
                    mTeacherId2AssignedClasses.get(s.getTeacherId()).add(fragment);

//                    log.info("getClassesAssignedToATeacherSolutionDuplicateWhenMultipleFragmentTimeTable, class " +
//                             s.getClassCode() + " has multiple timetable fragments.length = " +
//                             fragments.length + " ADD fragment " + fragment.getTimetable());
                }
            } else {
//                log.info("getClassesAssignedToATeacherSolutionDuplicateWhenMultipleFragmentTimeTable, class " +
//                         s.getClassCode() + " has ONLY ONE timetable");

                mTeacherId2AssignedClasses.get(s.getTeacherId()).add(s);
            }
        }

        // Fill in to prevent NULL in the next logic, also debug and test
        for (TeacherForAssignmentPlan t : teachersInPlan) {
//            log.info("getClassesAssignedToATeacherSolutionDuplicateWhenMultipleFragmentTimeTable, consider teacher " +
//                     t.getTeacherId());

            if (!teacherHasClasses.contains(t.getTeacherId())) {
//                log.info("getClassesAssignedToATeacherSolutionDuplicateWhenMultipleFragmentTimeTable, consider teacher " +
//                         t.getTeacherId() + " DOES NOT have classes assigned");

                mTeacherId2AssignedClasses.put(t.getTeacherId(), new ArrayList<>());
            }
        }

        //
        for (String teacherId : mTeacherId2AssignedClasses.keySet()) {
            ClassesAssignedToATeacherModel assignedModel = new ClassesAssignedToATeacherModel();

            //
            String teacherName = "";
            if (mId2Teacher.get(teacherId) != null) {
                teacherName = mId2Teacher.get(teacherId).getTeacherName();
            }
            assignedModel.setTeacherName(teacherName);

            assignedModel.setTeacherId(teacherId);
            assignedModel.setClassList(mTeacherId2AssignedClasses.get(teacherId));
//            assignedModel.setNumberOfClass(assignedModel.getClassList().size());

            // Not neccessary for grid view
//            double hourLoad = 0;
//            for (ClassTeacherAssignmentSolutionModel solutionModel : assignedModel.getClassList()) {
//                hourLoad += solutionModel.getHourLoad();
//            }
//            assignedModel.setHourLoad(hourLoad);

            //if(assignedModel.getClassList().size() == 0) continue;

            if (assignedModel.getClassList().size() > 0) {
                // Sort classes assigned to current teacher in an increasing order of time-table
                ClassTeacherAssignmentSolutionModel[] orderedAssignments = new ClassTeacherAssignmentSolutionModel[assignedModel
                    .getClassList().size()];

                // Calculate extra informations
                for (int i = 0; i < orderedAssignments.length; i++) {
                    orderedAssignments[i] = assignedModel.getClassList().get(i);
                    TimeTableStartAndDuration ttsd = TimetableConflictChecker.extractFromString(orderedAssignments[i].getTimetable());

                    if (ttsd == null) {
                        log.info("getSuggestedTeacherAndActionForClass, TimeTableStartAndDuration NULL for " +
                                 orderedAssignments[i].getTimetable() +
                                 " class " + orderedAssignments[i].getClassCode() +
                                 ", " + orderedAssignments[i].getCourseName() +
                                 " courseId = " + orderedAssignments[i].getCourseId());

                        orderedAssignments[i].setStartSlot(0);
                        orderedAssignments[i].setEndSlot(0);
                        orderedAssignments[i].setDuration(0);
                    } else {
                        orderedAssignments[i].setStartSlot(ttsd.getStartSlot());
                        orderedAssignments[i].setEndSlot(ttsd.getEndSlot());
                        orderedAssignments[i].setDuration(ttsd.getDuration());
                    }
                }

                // Sorting
                for (int i = 0; i < orderedAssignments.length; i++) {
                    // Remove unnecessary field
                    orderedAssignments[i].setSolutionItemId(null);
                    orderedAssignments[i].setTeacherId(null);
                    orderedAssignments[i].setTeacherName(null);

                    for (int j = i + 1; j < orderedAssignments.length; j++) {
                        if (orderedAssignments[i].getStartSlot() > orderedAssignments[j].getStartSlot()) {
                            ClassTeacherAssignmentSolutionModel tmp = orderedAssignments[i];
                            orderedAssignments[i] = orderedAssignments[j];
                            orderedAssignments[j] = tmp;
                        }
                    }
                }

                //
                orderedAssignments[0].setStartIndexFromPrevious(orderedAssignments[0].getStartSlot() - 1);
                for (int i = 1; i < orderedAssignments.length; i++) {
                    orderedAssignments[i]
                        .setStartIndexFromPrevious(orderedAssignments[i].getStartSlot() -
                                                   orderedAssignments[i - 1].getEndSlot() -
                                                   1);
                }

                //
                assignedModel.getClassList().clear();
                for (ClassTeacherAssignmentSolutionModel orderedAssignment : orderedAssignments) {
                    assignedModel.getClassList().add(orderedAssignment);
                }

                assignedModel
                    .setRemainEmptySlots(
                        72 - orderedAssignments[orderedAssignments.length - 1].getEndSlot()); // 72 = 6 * 12 periods
            }

            if (assignedModel.getClassList().size() > 0) {
                gridViewData.add(assignedModel);
            }
        }

        gridViewData.sort(Comparator.comparing(ClassesAssignedToATeacherModel::getTeacherId));
        return gridViewData;
    }

    @Override
    public TeacherClassAssignmentSolution assignTeacherToClass(UserLogin u, AssignTeacherToClassInputModel input) {
        TeacherClassAssignmentSolution teacherClassAssignmentSolution = new TeacherClassAssignmentSolution();
        teacherClassAssignmentSolution.setTeacherId(input.getTeacherId());
        teacherClassAssignmentSolution.setClassId(input.getClassId());
        teacherClassAssignmentSolution.setPlanId(input.getPlanId());
        teacherClassAssignmentSolution.setCreatedBy(u.getUserLoginId());
        teacherClassAssignmentSolution.setCreatedStamp(new Date());

        teacherClassAssignmentSolution = solutionRepo.save(teacherClassAssignmentSolution);

        return teacherClassAssignmentSolution;
    }

    /**
     * OK. Da them pinned cua TeacherClassAssignmentSolution
     *
     * @param userId
     * @param planId
     * @param input
     */
    @Transactional
    @Override
    public void assignTeacherToClass(String userId, UUID planId, AssignTeacherToClassInputModel input) {
        // planId and classId unique identify a solution but TeacherClassAssignmentSolution's id is solutionItemId,
        // so may have 2 solution for the same class in an assignment plan
        // Remove class s.getClassId() from current solution
        solutionRepo.deleteAllByPlanIdAndClassId(planId, input.getClassId());

        TeacherClassAssignmentSolution assignment = new TeacherClassAssignmentSolution();
        assignment.setClassId(input.getClassId());
        assignment.setPlanId(input.getPlanId());
        assignment.setTeacherId(input.getTeacherId());
        assignment.setPinned(input.isPinned());
        assignment.setCreatedBy(userId);
        assignment.setCreatedStamp(new Date());

        solutionRepo.save(assignment);
        log.info("assignTeacherToClass, ADD and SAVE assignment (" +
                 input.getClassId() + " - " + input.getTeacherId() + ") in plan " + input.getPlanId());
    }

    @Override
    public boolean removeClassTeacherAssignmentSolution(
        UserLogin u,
        RemoveClassTeacherAssignmentSolutionInputModel input
    ) {
        TeacherClassAssignmentSolution teacherClassAssignmentSolution
            = solutionRepo.findById(input.getSolutionItemId()).orElse(null);
        if (teacherClassAssignmentSolution == null) {
            return false;
        } else {
            solutionRepo.delete(teacherClassAssignmentSolution);
        }
        return true;
    }

    /**
     * OK
     *
     * @param planId
     * @param solutionItemIds
     * @return
     */
    @Override
    public void removeClassTeacherAssignmentSolutionList(UUID planId, UUID[] solutionItemIds) {
        for (UUID id : solutionItemIds) {
            TeacherClassAssignmentSolution s = solutionRepo.findById(id).orElse(null);
            if (null != s) {
                solutionRepo.delete(s);
                log.info("removeClassTeacherAssignmentSolutionList, delete " + s.getId());
            }
        }
    }

    /**
     * Ok
     *
     * @param planId
     * @return
     */
    @Override
    public List<ClassesAssignedToATeacherModel> getClassesAssignedToATeacherSolution(UUID planId) {
        List<ClassesAssignedToATeacherModel> assignedModels = new ArrayList<>();

        //
        List<EduTeacher> allTeachers = eduTeacherRepo.findAll();
        HashMap<String, EduTeacher> mId2Teacher = new HashMap<>();
        for (EduTeacher t : allTeachers) {
            mId2Teacher.put(t.getId(), t);
        }

        //
        HashMap<String, List<ClassTeacherAssignmentSolutionModel>> mTeacherId2Classes = new HashMap<>();
        List<ClassTeacherAssignmentSolutionModel> assignments = getClassTeacherAssignmentSolution(planId);
        for (ClassTeacherAssignmentSolutionModel s : assignments) {
            mTeacherId2Classes.computeIfAbsent(s.getTeacherId(), k -> new ArrayList<>());
            mTeacherId2Classes.get(s.getTeacherId()).add(s);
        }

        //
        for (String teacherId : mTeacherId2Classes.keySet()) {
            ClassesAssignedToATeacherModel model = new ClassesAssignedToATeacherModel();

            String teacherName = "";
            if (mId2Teacher.get(teacherId) != null) {
                teacherName = mId2Teacher.get(teacherId).getTeacherName();
            }
            model.setTeacherName(teacherName);

            model.setTeacherId(teacherId);
            model.setClassList(mTeacherId2Classes.get(teacherId));
            model.setNumberOfClass(model.getClassList().size());

            double hourLoad = 0;
            HashSet<Integer> D = new HashSet<>();
            for (ClassTeacherAssignmentSolutionModel solutionModel : model.getClassList()) {
                hourLoad += solutionModel.getHourLoad();
                HashSet<Integer> days = TimetableConflictChecker.extractDayOfTimeTable(solutionModel.getTimetable());

                if (null != days) {
                    D.addAll(days);
                }
            }

            model.setHourLoad(hourLoad);
            model.setNumberOfWorkingDays(D.size());

            assignedModels.add(model);
        }

        assignedModels.sort(Comparator.comparing(ClassesAssignedToATeacherModel::getTeacherId));
        return assignedModels;
    }

    /**
     * OK. Da xu ly them pinned vao TeacherClassAssignmentSolution
     *
     * @param planId
     * @return
     */
    @Override
    public List<ClassTeacherAssignmentSolutionModel> getClassTeacherAssignmentSolution(UUID planId) {
        List<TeacherClassAssignmentSolution> solutions = solutionRepo.findAllByPlanId(planId);

        List<EduCourse> courses = eduCourseRepo.findAll();
        HashMap<String, EduCourse> mCourseId2Course = new HashMap<>();
        for (EduCourse c : courses) {
            mCourseId2Course.put(c.getId(), c);
        }

        List<EduTeacher> teachers = eduTeacherRepo.findAll();
        HashMap<String, EduTeacher> mTeacherId2Teacher = new HashMap<>();
        for (EduTeacher t : teachers) {
            mTeacherId2Teacher.put(t.getId(), t);
        }

        List<ClassTeacherAssignmentClassInfo> classes = classInfoRepo.findAllByPlanId(planId);
        HashMap<String, ClassTeacherAssignmentClassInfo> mClassId2Class = new HashMap<>();
        for (ClassTeacherAssignmentClassInfo i : classes) {
            mClassId2Class.put(i.getClassId(), i);
        }

        List<ClassTeacherAssignmentSolutionModel> solutionModels = new ArrayList<>();
        for (TeacherClassAssignmentSolution s : solutions) {
            String classId = s.getClassId();
            ClassTeacherAssignmentClassInfo info = mClassId2Class.get(classId);

            if (info == null) {
                continue;
            }

            ClassTeacherAssignmentSolutionModel solutionModel = new ClassTeacherAssignmentSolutionModel();
            solutionModel.setSolutionItemId(s.getId());
            solutionModel.setClassCode(s.getClassId());
            solutionModel.setClassType(info.getClassType());
            solutionModel.setCourseId(info.getCourseId());
            solutionModel.setPinned(s.isPinned());

            EduCourse course = mCourseId2Course.get(info.getCourseId());
            if (null != course) {
                solutionModel.setCourseName(course.getName());
            } else {
                log.info("getClassTeacherAssignmentSolution, courseId " + info.getCourseId() + " null");
            }

            EduTeacher teacher = mTeacherId2Teacher.get(s.getTeacherId());

            solutionModel.setTeacherId(s.getTeacherId());
            solutionModel.setTeacherName(teacher.getTeacherName());
            solutionModel.setTimetable(info.getLesson());
//            solutionModel.setTimetableCode(info.getTimetable());
            solutionModel.setHourLoad(info.getHourLoad());

            solutionModels.add(solutionModel);
        }

        return solutionModels;
    }

    /**
     * OK
     *
     * @param planId
     * @param teacherCourses
     */
    @Transactional
    @Override
    public void addTeacherCourseToAssignmentPlan(UUID planId, AddTeacherCourse2PlanIM[] teacherCourses) {
//        log.info("addTeacherCourseToAssignmentPlan, planId = " + planId);
        Set<String> addedTeacherIds = new HashSet<>();
        for (AddTeacherCourse2PlanIM input : teacherCourses) {
            TeacherCourse tc = teacherCourseRepo.findByRefId(input.getTeacherCourseId());

            if (null != tc) {
                teacherCourseForAssignmentPlanRepo.save(new TeacherCourseForAssignmentPlan(
                    tc.getRefId(),
                    planId,
                    input.getPriority(),
                    tc.getTeacherId(),
                    tc.getCourseId(),
                    tc.getClassType()
                ));

                addedTeacherIds.add(tc.getTeacherId());
            }
        }

        // Auto add teacher to plan
        List<TeacherForAssignmentPlan> addedTeachers = new ArrayList<>();
        for (String id : addedTeacherIds) {
            TeacherForAssignmentPlan t = new TeacherForAssignmentPlan();
            t.setTeacherId(id);
            t.setMaxHourLoad(0);
            addedTeachers.add(t);
        }

        addTeacherToAssignmentPlan(planId, addedTeachers.toArray(new TeacherForAssignmentPlan[0]));
    }

    /**
     * Temporarily OK
     *
     * @param planId
     * @param teacherCourses
     */
    @Transactional
    @Override
    public void removeTeacherCourseFromAssignmentPlan(UUID planId, TeacherCourseForAssignmentPlan[] teacherCourses) {
        // Todo: test truong hop xoa solution
        if (teacherCourses.length > 0) {
            List<ClassTeacherAssignmentClassInfo> classes = classInfoRepo.findAllByPlanId(planId);
            HashMap<String, ClassTeacherAssignmentClassInfo> mClassId2Class = new HashMap<>();
            for (ClassTeacherAssignmentClassInfo c : classes) {
                mClassId2Class.put(c.getClassId(), c);
            }

            for (TeacherCourseForAssignmentPlan tc : teacherCourses) {
                // Remove related items in solution
                List<TeacherClassAssignmentSolution> solutions = solutionRepo.findAllByPlanIdAndTeacherId(
                    planId,
                    tc.getTeacherId());

                for (TeacherClassAssignmentSolution s : solutions) {
                    String classId = s.getClassId();
                    ClassTeacherAssignmentClassInfo c = mClassId2Class.get(classId);

                    if (c != null) {
                        if (c.getCourseId().equalsIgnoreCase(tc.getCourseId())
                            && c.getClassType().equalsIgnoreCase(tc.getClassType())) {
                            solutionRepo.delete(s);
//                            log.info("removeTeacherCourseFromAssignmentPlan, remove assignment(" +
//                                     classId + "[" + c.getCourseId() + ", " + c.getClassType() + "] - " +
//                                     tc.getTeacherId() + ") in plan " + planId);
                        }
                    }
                }

                teacherCourseForAssignmentPlanRepo.delete(tc);
//                log.info("removeTeacherCourseFromAssignmentPlan, remove (" +
//                         tc.getTeacherId() + ", " +
//                         tc.getCourseId() + ", " +
//                         tc.getClassType() + ", " +
//                         planId + ")");

            }
        }
    }

    /**
     * OK
     *
     * @param planId
     * @return
     */
    @Override
    public List<PairOfConflictTimetableClassModel> getPairOfConflictTimetableClass(UUID planId) {
        List<ClassTeacherAssignmentClassInfo> classes = classInfoRepo.findAllByPlanId(planId);
        List<PairOfConflictTimetableClassModel> pairs = new ArrayList<>();

        for (int i = 0; i < classes.size(); i++) {
            for (int j = i + 1; j < classes.size(); j++) {
                ClassTeacherAssignmentClassInfo c1 = classes.get(i);
                ClassTeacherAssignmentClassInfo c2 = classes.get(j);

                if (TimetableConflictChecker.conflict(c1.getLesson(), c2.getLesson())) {
                    PairOfConflictTimetableClassModel p = new PairOfConflictTimetableClassModel();
                    p.setClassId1(c1.getClassId());
                    p.setCourseId1(c1.getCourseId());
                    p.setTimetable1(c1.getTimetable());
                    p.setTimetableCode1(c1.getLesson());

                    p.setClassId2(c2.getClassId());
                    p.setCourseId2(c2.getCourseId());
                    p.setTimetable2(c2.getTimetable());
                    p.setTimetableCode2(c2.getLesson());

                    pairs.add(p);
                }
            }
        }

        return pairs;
    }

    /**
     * OK
     *
     * @param u
     * @param planId
     * @param classId
     * @param input
     */
    @Transactional
    @Override
    public void updateClassForAssignment(
        UserLogin u,
        UUID planId,
        String classId,
        UpdateClassForAssignmentInputModel input
    ) {
        ClassTeacherAssignmentClassInfo c = classInfoRepo.findByClassIdAndPlanId(classId, planId);
        if (null != c) {
            c.setHourLoad(input.getHourLoad());
            classInfoRepo.save(c);
        }

        solutionRepo.deleteAllByPlanIdAndClassId(planId, classId);
    }

    /**
     * OK
     *
     * @param input
     * @return
     */
    @Transactional
    @Override
    public void updateTeacherForAssignment(
        UUID planId,
        String teacherId,
        UpdateTeacherForAssignmentInputModel input
    ) {
        TeacherForAssignmentPlan teacherForPlan = teacherForAssignmentPlanRepo.findByTeacherIdAndPlanId(
            teacherId,
            planId);

        if (teacherForPlan != null) {
            // Remove all solution items of teacher if any parameter change
            if (teacherForPlan.getMaxHourLoad() != input.getHourLoad() ||
                !teacherForPlan.getMinimizeNumberWorkingDays().equalsIgnoreCase(input.getMinimizeNumberWorkingDays())
            ) {
                solutionRepo.deleteAllByPlanIdAndTeacherId(planId, teacherId);
            }

            teacherForPlan.setMaxHourLoad(input.getHourLoad());
            teacherForPlan.setMinimizeNumberWorkingDays(input.getMinimizeNumberWorkingDays());
            teacherForAssignmentPlanRepo.save(teacherForPlan);
        }
    }

    @Override
    public void updateTeacherCourseForAssignmentPlan(
        UserLogin u,
        TeacherCourseForAssignmentPlan teacherCourse
    ) {
        TeacherCourseForAssignmentPlan tc = teacherCourseForAssignmentPlanRepo
            .findByPlanIdAndTeacherCourseId(teacherCourse.getPlanId(), teacherCourse.getTeacherCourseId());

        if (null != tc) {
            teacherCourseForAssignmentPlanRepo.save(teacherCourse);

            log.info("updateTeacherCourseForAssignmentPlan, update OK!!");
        }
    }

    /**
     * OK
     *
     * @param planId
     * @return
     */
    @Override
    public List<ConflictClassAssignedToTeacherModel> getConflictClassesInSolution(UUID planId) {
        List<ClassTeacherAssignmentSolutionModel> solutionModels = getClassTeacherAssignmentSolution(planId);

        log.info("getConflictClassesAssignedToTeacherInSolution, planId = " + planId +
                 ", sol.sz = " + solutionModels.size());

        List<ConflictClassAssignedToTeacherModel> conflictModels = new ArrayList<>();
        for (int i = 0; i < solutionModels.size(); i++) {
            ClassTeacherAssignmentSolutionModel si = solutionModels.get(i);

            for (int j = i + 1; j < solutionModels.size(); j++) {
                ClassTeacherAssignmentSolutionModel sj = solutionModels.get(j);

                if (si.getTeacherId().equals(sj.getTeacherId())) {
                    boolean conflict = TimetableConflictChecker.conflictMultiTimeTable(
                        si.getTimetable(),
                        sj.getTimetable());

//                    log.info("getConflictClassesAssignedToTeacherInSolution, timetable1 = " +
//                             si.getTimetable() + " timetable2 = " + sj.getTimetable() + " conflict = " + conflict);

                    if (conflict) {
                        ConflictClassAssignedToTeacherModel cm = new ConflictClassAssignedToTeacherModel();

                        cm.setTeacherId(si.getTeacherId());
                        cm.setTeacherName(si.getTeacherName());
                        cm.setClassCode1(si.getClassCode());
                        cm.setCourseName1(si.getCourseName());
                        cm.setTimeTable1(si.getTimetable());
                        cm.setClassCode2(sj.getClassCode());
                        cm.setCourseName2(sj.getCourseName());
                        cm.setTimeTable2(sj.getTimetable());

                        conflictModels.add(cm);
                    }
                }
            }
        }

        log.info("getConflictClassesAssignedToTeacherInSolution, conflict list.sz = " + conflictModels.size());

        return conflictModels;
    }
}
