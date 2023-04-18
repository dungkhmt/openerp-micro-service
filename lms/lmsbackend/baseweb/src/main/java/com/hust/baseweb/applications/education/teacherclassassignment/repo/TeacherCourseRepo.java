package com.hust.baseweb.applications.education.teacherclassassignment.repo;

import com.hust.baseweb.applications.education.teacherclassassignment.entity.TeacherCourse;
import com.hust.baseweb.applications.education.teacherclassassignment.entity.compositeid.TeacherCourseId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TeacherCourseRepo extends JpaRepository<TeacherCourse, TeacherCourseId> {

    TeacherCourse findByRefId(UUID id);

//    List<TeacherCourse> findByCourseIdAndClassType(String courseId, String classType);

}
