package com.hust.baseweb.applications.education.teacherclassassignment.repo;

import com.hust.baseweb.applications.education.teacherclassassignment.entity.TeacherCourseForAssignmentPlan;
import com.hust.baseweb.applications.education.teacherclassassignment.entity.compositeid.TeacherCoursePlanId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TeacherCourseForAssignmentPlanRepo
    extends JpaRepository<TeacherCourseForAssignmentPlan, TeacherCoursePlanId> {

    List<TeacherCourseForAssignmentPlan> findAllByPlanId(UUID planId);

    TeacherCourseForAssignmentPlan findByPlanIdAndTeacherCourseId(UUID planId, UUID teacherCourseId);

    void deleteByPlanIdAndTeacherId(UUID planId, String teacherId);

    List<TeacherCourseForAssignmentPlan> findAllByPlanIdAndCourseIdAndClassType(
        UUID planId,
        String courseId,
        String classType
    );
}
