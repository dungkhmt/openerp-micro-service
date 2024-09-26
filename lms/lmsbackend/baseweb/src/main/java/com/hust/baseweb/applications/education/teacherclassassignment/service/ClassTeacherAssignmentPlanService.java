package com.hust.baseweb.applications.education.teacherclassassignment.service;

import com.hust.baseweb.applications.education.teacherclassassignment.entity.*;
import com.hust.baseweb.applications.education.teacherclassassignment.model.*;
import com.hust.baseweb.applications.education.teacherclassassignment.model.teachersuggestion.SuggestedTeacherAndActionForClass;
import com.hust.baseweb.entity.UserLogin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ClassTeacherAssignmentPlanService {

    ClassTeacherAssignmentPlan create(UserLogin u, ClassTeacherAssignmentPlanCreateModel input);

    List<ClassTeacherAssignmentPlan> findAll();

    ClassTeacherAssignmentPlanDetailModel getClassTeacherAssignmentPlanDetail(UUID planId);

    List<ClassInfoForAssignment2TeacherModel> findClassesInPlan(UUID planId);

    boolean extractExcelAndStoreDB(UUID planId, MultipartFile file);

    String addTeacher(EduTeacher teacher);

    List<EduTeacher> findAllTeachers();

    Page<EduTeacher> findAllTeachersByPage(String keyword, Pageable pageable);

    List<TeacherForAssignmentPlan> findAllTeacherByPlanId(UUID planId);

    void addTeacherToAssignmentPlan(UUID planId, TeacherForAssignmentPlan[] addedTeachers);

    void removeTeacherFromAssignmentPlan(UUID planId, String[] teacherIds);

    void removeClassFromAssignmentPlan(UUID planId, String[] classIds);

    List<TeacherCourse> findAllTeacherCourse();

    List<TeacherCourseForAssignmentPlan> findTeacherCourseOfPlan(UUID planId);

    boolean extractExcelAndStoreDBTeacherCourse(UUID planId, String choice, MultipartFile file);

    boolean autoAssignTeacher2Class(RunAutoAssignTeacher2ClassInputModel input);

    List<ClassTeacherAssignmentSolutionModel> getNotAssignedClassSolution(UUID planId);

    // Don't use this logic anymore, consider removing in future
//    List<SuggestedTeacherForClass> getSuggestedTeacherForClass(String classId, UUID planId);

    List<SuggestedTeacherAndActionForClass> getSuggestedTeacherAndActionForClass(String classId, UUID planId);

    List<ClassesAssignedToATeacherModel> getClassesAssignedToATeacherSolutionDuplicateWhenMultipleFragmentTimeTable(UUID planId);

    void assignTeacherToClass(String userId, UUID planId, AssignTeacherToClassInputModel input);

    TeacherClassAssignmentSolution assignTeacherToClass(UserLogin u, AssignTeacherToClassInputModel input);

    boolean removeClassTeacherAssignmentSolution(
        UserLogin u,
        RemoveClassTeacherAssignmentSolutionInputModel input
    );

    void removeClassTeacherAssignmentSolutionList(UUID planId, UUID[] solutionItemIds);

    List<ClassesAssignedToATeacherModel> getClassesAssignedToATeacherSolution(UUID planId);

    List<ClassTeacherAssignmentSolutionModel> getClassTeacherAssignmentSolution(UUID planId);

    void addTeacherCourseToAssignmentPlan(UUID planId, AddTeacherCourse2PlanIM[] teacherCourses);

    void removeTeacherCourseFromAssignmentPlan(UUID planId, TeacherCourseForAssignmentPlan[] teacherCourses);

    List<PairOfConflictTimetableClassModel> getPairOfConflictTimetableClass(UUID planId);

    void updateClassForAssignment(
        UserLogin u,
        UUID planId,
        String classId,
        UpdateClassForAssignmentInputModel input
    );

    void updateTeacherForAssignment(
        UUID planId,
        String teacherId,
        UpdateTeacherForAssignmentInputModel input
    );

    void updateTeacherCourseForAssignmentPlan(
        UserLogin u,
        TeacherCourseForAssignmentPlan teacherCourse
    );

    List<ConflictClassAssignedToTeacherModel> getConflictClassesInSolution(UUID planId);
}
