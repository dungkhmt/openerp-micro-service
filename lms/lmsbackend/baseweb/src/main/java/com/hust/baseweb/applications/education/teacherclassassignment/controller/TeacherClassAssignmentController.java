package com.hust.baseweb.applications.education.teacherclassassignment.controller;

import com.hust.baseweb.applications.education.teacherclassassignment.entity.ClassTeacherAssignmentPlan;
import com.hust.baseweb.applications.education.teacherclassassignment.entity.EduTeacher;
import com.hust.baseweb.applications.education.teacherclassassignment.entity.TeacherCourse;
import com.hust.baseweb.applications.education.teacherclassassignment.model.*;
import com.hust.baseweb.applications.education.teacherclassassignment.service.ClassTeacherAssignmentPlanService;
import com.hust.baseweb.applications.education.teacherclassassignment.service.TeacherClassAssignmentAlgoService;
import com.hust.baseweb.entity.UserLogin;
import com.hust.baseweb.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Log4j2
@Controller
@Validated
@AllArgsConstructor(onConstructor__ = @Autowired)
public class TeacherClassAssignmentController {

    private TeacherClassAssignmentAlgoService algoService;

//    private TeacherClassAssignmentService assignmentService;

    private UserService userService;

    private ClassTeacherAssignmentPlanService planService;

    @GetMapping("/get-all-class-teacher-assignment-plan")
    public ResponseEntity<?> getAllClassTeacherAssignmentPlan(Principal principal) {
        UserLogin u = userService.findById(principal.getName());
        List<ClassTeacherAssignmentPlan> classTeacherAssignmentPlanList = planService.findAll();
        return ResponseEntity.ok().body(classTeacherAssignmentPlanList);
    }

    @Secured({"ROLE_EDUCATION_TEACHING_MANAGEMENT_TEACHER"})
    @PostMapping("/add-teacher")
    public ResponseEntity<?> addTeacher(Principal principal, @RequestBody EduTeacher teacher) {
        String result = planService.addTeacher(teacher);
        log.info("addTeacher, teacherId " + teacher.getId());
        if (result.equals("OK")) {
            return ResponseEntity.ok(result);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    /**
     * OK
     *
     * @return
     */
    @GetMapping("/edu/teaching-assignment/teacher")
    public ResponseEntity<?> getAllTeachers() {
        List<EduTeacher> eduTeachers = planService.findAllTeachers();
        return ResponseEntity.ok().body(eduTeachers);
    }

    @GetMapping("/get-all-teachers-by-page")
    public ResponseEntity<?> getTeachersByPage(
        Principal principal,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int pageSize,
        @RequestParam(defaultValue = "teacherName") String sortBy,
        @RequestParam(defaultValue = "asc") String sortType,
        @RequestParam(defaultValue = "_") String keyword
    ) {
        Sort sort = Sort.by(sortBy);
        if (sortType.equals("desc")) {
            sort = sort.descending();
        }
        Pageable paging = PageRequest.of(page, pageSize, sort);
        Page<EduTeacher> eduTeacherPage = planService.findAllTeachersByPage(keyword, paging);
        return ResponseEntity.ok(eduTeacherPage);
    }

    /**
     * OK
     *
     * @return
     */
    @GetMapping("/edu/teaching-assignment/teacher-course")
    public ResponseEntity<?> getAllTeacherCourse() {
        List<TeacherCourse> teacherCourses = planService.findAllTeacherCourse();
        return ResponseEntity.ok().body(teacherCourses);
    }

    @PostMapping("/auto-assign-teacher-2-class")
    public ResponseEntity<?> autoAssignTeacher2Class(
        @RequestBody RunAutoAssignTeacher2ClassInputModel input
    ) {
        planService.autoAssignTeacher2Class(input);
        return ResponseEntity.ok().body("OK");
    }

    /**
     * Temporarily disabled
     *
     * @param principal
     * @param planId
     * @return
     */
//    @PutMapping("/edu/teaching-assignment/plan/{planId}/teacher-course")
//    public ResponseEntity<?> updateTeacherCourseForAssignmentPlan(
//        Principal principal,
//        @PathVariable UUID planId,
//        @RequestBody TeacherCourseForAssignmentPlan teacherCourse
//    ) {
//        UserLogin u = userService.findById(principal.getName());
//        log.info("updateTeacherCourseForAssignmentPlan, planId = " + teacherCourse.getPlanId() +
//                 " teacherId = " + teacherCourse.getTeacherId() +
//                 " courseId = " + teacherCourse.getCourseId() +
//                 " priority = " + teacherCourse.getPriority());
//
//        classTeacherAssignmentPlanService.updateTeacherCourseForAssignmentPlan(u, teacherCourse);
//
//        return ResponseEntity.ok().body("OK)");
//    }

    /**
     * Don't use this API anymore, consider removing in future
     */
//    @GetMapping("/get-suggested-teacher-for-class/{classId}/{planId}")
//    public ResponseEntity<?> getSuggestedTeacherForClass(
//        @PathVariable String classId,
//        @PathVariable UUID planId
//    ) {
//        log.info("getSuggestedTeacherForClass, classId = " + classId);
//        List<SuggestedTeacherForClass> lst = planService.getSuggestedTeacherForClass(
//            classId,
//            planId);
//
//        return ResponseEntity.ok().body(lst);
//    }
    @PostMapping("/manual-remove-class-teacher-assignment-solution")
    public ResponseEntity<?> manualRemoveClassTeacherAssignmentSolution(
        Principal principal,
        @RequestBody RemoveClassTeacherAssignmentSolutionInputModel input
    ) {
        UserLogin u = userService.findById(principal.getName());
        log.info("manualRemoveClassTeacherAssignmentSolution, solutionItemId = " + input.getSolutionItemId());
        boolean ok = planService.removeClassTeacherAssignmentSolution(u, input);
        return ResponseEntity.ok().body(ok);
    }

    /**
     * Don't use this API anymore, consider removing in future
     */
//    @PostMapping("/manual-assign-teacher-to-class")
//    public ResponseEntity<?> manualAssignTeacherToClass(
//        Principal principal,
//        @RequestBody AssignTeacherToClassInputModel input
//    ) {
//        UserLogin u = userService.findById(principal.getName());
//        log.info("manualAssignTeacherToClass, planId = " + input.getPlanId() + " teacherId = " + input.getTeacherId()
//                 + " classId = " + input.getClassId());
//
//        TeacherClassAssignmentSolution teacherClassAssignmentSolution = planService.assignTeacherToClass(
//            u,
//            input);
//        return ResponseEntity.ok().body(teacherClassAssignmentSolution);
//    }
    @GetMapping("/get-class-teacher-assignment-plan/detail/{planId}")
    public ResponseEntity<?> getClassTeacherAssignmentPlanDetail(Principal principal, @PathVariable UUID planId) {
        log.info("getClassTeacherAssignmentPlanDetail, planId = " + planId);
        ClassTeacherAssignmentPlanDetailModel classTeacherAssignmentPlanDetailModel = planService
            .getClassTeacherAssignmentPlanDetail(planId);
        return ResponseEntity.ok().body(classTeacherAssignmentPlanDetailModel);
    }

    @PostMapping("/create-class-teacher-assignment-plan")
    public ResponseEntity<?> createClassTeacherAssignmentPlan(
        Principal principal, @RequestBody
        ClassTeacherAssignmentPlanCreateModel input
    ) {
        UserLogin u = userService.findById(principal.getName());
        log.info("createClassTeacherAssignmentPlan, planName   = " + input.getPlanName());
        ClassTeacherAssignmentPlan classTeacherAssignmentPlan = planService.create(u, input);
        return ResponseEntity.ok().body(classTeacherAssignmentPlan);

    }

    @PostMapping("/teacherclassassignment/algo")
    public ResponseEntity<?> computeTeacherClassAssignment(
        Principal principal, @RequestBody
        AlgoTeacherAssignmentIM input
    ) {
        System.out.println("computeTeacherClassAssignment start");
        TeacherClassAssignmentOM teacherClassAssignmentOM = algoService.computeTeacherClassAssignment(
            input);

        return ResponseEntity.ok().body(teacherClassAssignmentOM);
    }

    /**
     * Old API, consider removing this API in future
     */
//    @PostMapping("/teacher-class-assignment/mip")
//    public ResponseEntity<?> assign(@RequestBody AlgoTeacherAssignmentIM input) {
//        return ResponseEntity.ok().body(assignmentService.assign(input));
//    }
}
