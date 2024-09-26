package com.hust.baseweb.applications.education.teacherclassassignment.controller;

import com.hust.baseweb.applications.education.teacherclassassignment.entity.TeacherCourseForAssignmentPlan;
import com.hust.baseweb.applications.education.teacherclassassignment.entity.TeacherForAssignmentPlan;
import com.hust.baseweb.applications.education.teacherclassassignment.model.*;
import com.hust.baseweb.applications.education.teacherclassassignment.model.teachersuggestion.SuggestedTeacherAndActionForClass;
import com.hust.baseweb.applications.education.teacherclassassignment.service.ClassTeacherAssignmentPlanService;
import com.hust.baseweb.applications.education.teacherclassassignment.service.ClassTeacherAssignmentSolutionExcelExporter;
import com.hust.baseweb.entity.UserLogin;
import com.hust.baseweb.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Log4j2
@Controller
@Validated
@AllArgsConstructor(onConstructor__ = @Autowired)
@RequestMapping("/edu/teaching-assignment/plan")
public class PlanController {

    private UserService userService;

    private ClassTeacherAssignmentPlanService planService;

    /**
     * OK
     *
     * @param planId
     * @return
     */
    @GetMapping("/{planId}/teacher")
    public ResponseEntity<?> getTeacherForAssignmentPlan(@PathVariable UUID planId) {
        return ResponseEntity.ok().body(planService.findAllTeacherByPlanId(planId));
    }

    /**
     * OK
     *
     * @param planId
     * @return
     */
    @PostMapping("/{planId}/teacher")
    public ResponseEntity<?> addTeacherToAssignmentPlan(
        @PathVariable UUID planId,
        @RequestBody TeacherForAssignmentPlan[] teachers
    ) {
//        log.info("addTeacherToAssignmentPlan, planId = " + planId);
        planService.addTeacherToAssignmentPlan(planId, teachers);
        return ResponseEntity.ok().body("OK");
    }

    /**
     * OK
     *
     * @param planId
     * @param teacherIds
     * @return
     */
    @DeleteMapping("/{planId}/teacher")
    public ResponseEntity<?> removeTeacherFromAssignmentPlan(
        @PathVariable UUID planId,
        @RequestBody String[] teacherIds
    ) {
//        log.info("removeTeacherFromAssignmentPlan, planId = " + planId);
        planService.removeTeacherFromAssignmentPlan(planId, teacherIds);
        return ResponseEntity.ok().body("OK");
    }

    /**
     * OK
     *
     * @param planId
     * @param teacherId
     * @param input
     * @return
     */
    @PutMapping("/{planId}/teacher/{teacherId}")
    public ResponseEntity<?> updateTeacherForAssignment(
        @PathVariable UUID planId,
        @PathVariable String teacherId,
        @RequestBody UpdateTeacherForAssignmentInputModel input
    ) {
        planService.updateTeacherForAssignment(
            planId,
            teacherId,
            input);
        return ResponseEntity.ok().body("OK");
    }

    /**
     * OK
     *
     * @param planId
     * @return
     */
    @GetMapping("/{planId}/teacher-course")
    public ResponseEntity<?> getTeacherCourseForAssignment(@PathVariable UUID planId) {
        return ResponseEntity.ok().body(planService.findTeacherCourseOfPlan(planId));
    }

    /**
     * OK
     *
     * @param input
     * @return
     */
    @PostMapping("/{planId}/teacher-course")
    public ResponseEntity<?> addTeacherCourseToAssignmentPlan(
        @PathVariable UUID planId,
        @RequestBody AddTeacherCourse2PlanIM[] input
    ) {
        planService.addTeacherCourseToAssignmentPlan(planId, input);
        return ResponseEntity.ok().body("OK");
    }

    /**
     * OK
     *
     * @param planId
     * @param teacherCourses
     * @return
     */
    @DeleteMapping("/{planId}/teacher-course")
    public ResponseEntity<?> removeTeacherCourseFromAssignmentPlan(
        @PathVariable UUID planId,
        @RequestBody TeacherCourseForAssignmentPlan[] teacherCourses
    ) {
        planService.removeTeacherCourseFromAssignmentPlan(planId, teacherCourses);
        return ResponseEntity.ok().body("OK");
    }

    /**
     * Temporarily OK
     *
     * @param planId
     * @param file
     * @return
     */
    @PostMapping("/{planId}/teacher-course/upload-excel")
    public ResponseEntity<?> uploadExcelTeacherCourse(
        @PathVariable UUID planId,
        @RequestParam("file") MultipartFile file
    ) {
        log.info("uploadExcelTeacherCourse");
        boolean ok = planService.extractExcelAndStoreDBTeacherCourse(planId, "", file);
        return ResponseEntity.ok().body(ok);
    }

    /**
     * OK
     *
     * @param planId
     * @return
     */
    @GetMapping("/{planId}/class")
    public ResponseEntity<?> getClassListForAssignment2Teacher(@PathVariable UUID planId) {
//        log.info("getClassListForAssignment2Teacher, planId = " + planId);
        List<ClassInfoForAssignment2TeacherModel> classTeacherAssignmentClassInfos =
            planService.findClassesInPlan(planId);
        return ResponseEntity.ok().body(classTeacherAssignmentClassInfos);
    }

    /**
     * OK
     *
     * @param planId
     * @param classIds
     * @return
     */
    @DeleteMapping("/{planId}/class")
    public ResponseEntity<?> removeClassFromAssignmentPlan(
        @PathVariable UUID planId,
        @RequestBody String[] classIds
    ) {
//        log.info("removeClassFromAssignmentPlan, planId = " + planId);
        planService.removeClassFromAssignmentPlan(planId, classIds);
        return ResponseEntity.ok().body("OK");
    }

    /**
     * OK
     *
     * @param principal
     * @param planId
     * @param classId
     * @param input
     * @return
     */
    @PutMapping("/{planId}/class/{classId}")
    public ResponseEntity<?> updateClassForAssignment(
        Principal principal,
        @PathVariable UUID planId, @PathVariable String classId,
        @RequestBody UpdateClassForAssignmentInputModel input
    ) {
        UserLogin u = userService.findById(principal.getName());
        planService.updateClassForAssignment(u, planId, classId, input);
        return ResponseEntity.ok().body("OK");
    }

    /**
     * Temporarily OK
     *
     * @param planId
     * @param file
     * @return
     */
    @PostMapping("/{planId}/class/upload-excel")
    public ResponseEntity<?> uploadExcelClass4TeacherAssignment(
        @PathVariable UUID planId,
        @RequestParam("file") MultipartFile file
    ) {
        log.info("uploadExcelClass4TeacherAssignment, planId = " + planId);
        boolean ok = planService.extractExcelAndStoreDB(planId, file);
        return ResponseEntity.ok().body(ok);

    }

    /**
     * OK
     *
     * @param planId
     * @return
     */
    @GetMapping("/{planId}/class/pair-of-conflict-timetable-class")
    public ResponseEntity<?> getPairOfConflictTimetableClass(@PathVariable UUID planId) {
        List<PairOfConflictTimetableClassModel> pairs = planService.getPairOfConflictTimetableClass(
            planId);
//        log.info("getPairOfConflictTimetableClass, return list.sz = " + pairs.size());
        return ResponseEntity.ok().body(pairs);
    }

    /**
     * Temporarily OK. Upgrade after defending
     *
     * @param classId
     * @param planId
     * @return
     */
    @GetMapping("/{planId}/class/{classId}/suggested-teacher-and-actions")
    public ResponseEntity<?> getSuggestedTeacherAndActionsForClass(
        @PathVariable String classId,
        @PathVariable UUID planId
    ) {
        log.info("getSuggestedTeacherAndActionForClass, classId = " + classId + " planId = " + planId);
        List<SuggestedTeacherAndActionForClass> suggestedTeachers = planService
            .getSuggestedTeacherAndActionForClass(classId, planId);
        return ResponseEntity.ok().body(suggestedTeachers);
    }

    /**
     * OK
     *
     * @param planId
     * @return
     */
    @GetMapping("/{planId}/class/not-assigned-class")
    public ResponseEntity<?> getNotAssignedClassSolution(@PathVariable UUID planId) {
        List<ClassTeacherAssignmentSolutionModel> notAssignedClasses = planService.getNotAssignedClassSolution(planId);
        log.info("getNotAssignedClassSolution, return list.sz = " + notAssignedClasses.size());
        return ResponseEntity.ok().body(notAssignedClasses);
    }

    /**
     * OK. Da xu ly them pinned vao TeacherClassAssignmentSolution
     *
     * @param planId
     * @return
     */
    @GetMapping("/{planId}/solution")
    public ResponseEntity<?> getClassTeacherAssignmentSolutions(@PathVariable UUID planId) {
        log.info("getClassTeacherAssignmentSolutions, planId = " + planId);
        List<ClassTeacherAssignmentSolutionModel> assignments = planService.getClassTeacherAssignmentSolution(
            planId);
        return ResponseEntity.ok().body(assignments);
    }

    /**
     * OK. Da them pinned cua TeacherClassAssignmentSolution
     *
     * @param planId
     * @param input
     * @return
     */
    @PutMapping("/{planId}/solution")
    public ResponseEntity<?> manualAssignTeacherToClass(
        @CurrentSecurityContext(expression = "authentication.name") String userId,
        @PathVariable UUID planId,
        @RequestBody AssignTeacherToClassInputModel input
    ) {
        // class input.getClassId() was assigned to a teacher t
        // now remove class input.getClassId() from t, and re-assign to teacher input.getTeacherId()
        log.info("manualAssignTeacherToClass, planId = " + planId +
                 " teacherId = " + input.getTeacherId() + " classId = " + input.getClassId());

        planService.assignTeacherToClass(userId, planId, input);
        return ResponseEntity.ok().body("OK");
    }

    /**
     * OK
     *
     * @param planId
     * @param solutionItemIds
     * @return
     */
    @DeleteMapping("/{planId}/solution")
    public ResponseEntity<?> removeClassTeacherAssignmentSolutionList(
        @PathVariable UUID planId,
        @RequestBody UUID[] solutionItemIds
    ) {
        log.info("removeClassFromAssignmentPlan, planId = " + planId);

        planService.removeClassTeacherAssignmentSolutionList(planId, solutionItemIds);
        return ResponseEntity.ok().body("OK");
    }

    /**
     * OK
     *
     * @param planId
     * @return
     */
    @GetMapping("/{planId}/solution/teacher")
    public ResponseEntity<?> getClassesAssignedToATeacherSolution(@PathVariable UUID planId) {
        List<ClassesAssignedToATeacherModel> assignedModels = planService.getClassesAssignedToATeacherSolution(
            planId);
        return ResponseEntity.ok().body(assignedModels);
    }

    /**
     * OK
     *
     * @param planId
     * @return
     */
    @GetMapping("/{planId}/solution/conflict-classes")
    public ResponseEntity<?> getConflictClassesAssignedToTeacherInSolution(
        @PathVariable UUID planId
    ) {
        return ResponseEntity.ok().body(planService.getConflictClassesInSolution(planId));
    }

    /**
     * OK
     *
     * @param planId
     * @return
     */
    @GetMapping("/{planId}/solution/grid-view")
    public ResponseEntity<?> getClassesAssignedToATeacherSolutionForViewGrid(
        @PathVariable UUID planId
    ) {
        log.info("getClassesAssignedToATeacherSolutionForViewGrid, planId = " + planId);
        List<ClassesAssignedToATeacherModel> gridViewData = planService
            .getClassesAssignedToATeacherSolutionDuplicateWhenMultipleFragmentTimeTable(planId);
        return ResponseEntity.ok().body(gridViewData);
    }

    /**
     * OK
     *
     * @param planId
     * @return
     */
    @GetMapping(value = "/{planId}/solution/export-excel")
    public ResponseEntity<?> exportExcelClassTeacherAssignmentSolution(@PathVariable UUID planId) {
        log.info("exportExcelClassTeacherAssignmentSolution, planId = " + planId);

        ClassTeacherAssignmentPlanDetailModel plan = planService.getClassTeacherAssignmentPlanDetail(
            planId);
        List<ClassesAssignedToATeacherModel> gridViewData = planService.getClassesAssignedToATeacherSolutionDuplicateWhenMultipleFragmentTimeTable(
            planId);
        List<ClassTeacherAssignmentSolutionModel> solution = planService.getClassTeacherAssignmentSolution(
            planId);

        ClassTeacherAssignmentSolutionExcelExporter exporter = new ClassTeacherAssignmentSolutionExcelExporter(
            gridViewData,
            solution);

        ByteArrayInputStream in = exporter.toExcel();
        HttpHeaders headers = new HttpHeaders();

        headers.add("Content-Type", "application/octet-stream");
        headers.add("Content-Disposition", "attachment; filename=" + plan.getPlanName() + ".xlsx");
        headers.add("Access-Control-Expose-Headers", "Content-Disposition");

        return ResponseEntity
            .ok()
            .headers(headers)
            .body(new InputStreamResource(in));

        // Todo: Phan ben duoi tam thoi giu lai, xem xet sau
        /*
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=phan_cong_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        List<ClassTeacherAssignmentSolutionModel> solutions =
            classTeacherAssignmentPlanService.getClassTeacherAssignmentSolution(planId);

        ClassTeacherAssignmentSolutionExcelExporter exporter
            = new ClassTeacherAssignmentSolutionExcelExporter(solutions);

        try {
            exporter.export(response);
        }catch(Exception e){
            e.printStackTrace();
        }
        */
    }

}
