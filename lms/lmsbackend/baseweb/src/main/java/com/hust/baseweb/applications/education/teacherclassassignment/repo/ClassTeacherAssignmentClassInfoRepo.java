package com.hust.baseweb.applications.education.teacherclassassignment.repo;

import com.hust.baseweb.applications.education.teacherclassassignment.entity.ClassTeacherAssignmentClassInfo;
import com.hust.baseweb.applications.education.teacherclassassignment.entity.compositeid.ClassId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ClassTeacherAssignmentClassInfoRepo extends JpaRepository<ClassTeacherAssignmentClassInfo, ClassId> {

    List<ClassTeacherAssignmentClassInfo> findAllByPlanId(UUID planId);

    List<ClassTeacherAssignmentClassInfo> findByClassId(String classId);

    ClassTeacherAssignmentClassInfo findByClassIdAndPlanId(String classId, UUID planId);

    void deleteByPlanIdAndClassId(UUID planId, String classId);

}
