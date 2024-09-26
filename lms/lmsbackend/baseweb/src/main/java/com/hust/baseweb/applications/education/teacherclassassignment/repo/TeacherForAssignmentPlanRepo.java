package com.hust.baseweb.applications.education.teacherclassassignment.repo;

import com.hust.baseweb.applications.education.teacherclassassignment.entity.TeacherForAssignmentPlan;
import com.hust.baseweb.applications.education.teacherclassassignment.entity.compositeid.TeacherPlanId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TeacherForAssignmentPlanRepo extends JpaRepository<TeacherForAssignmentPlan, TeacherPlanId> {

    List<TeacherForAssignmentPlan> findAllByPlanId(UUID planId);

    TeacherForAssignmentPlan findByTeacherIdAndPlanId(String teacherId, UUID planId);
}
