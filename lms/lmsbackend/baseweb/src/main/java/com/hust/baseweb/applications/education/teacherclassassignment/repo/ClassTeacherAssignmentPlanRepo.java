package com.hust.baseweb.applications.education.teacherclassassignment.repo;

import com.hust.baseweb.applications.education.teacherclassassignment.entity.ClassTeacherAssignmentPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClassTeacherAssignmentPlanRepo extends JpaRepository<ClassTeacherAssignmentPlan, UUID> {

}
