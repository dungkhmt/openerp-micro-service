package com.hust.baseweb.applications.education.repo;

import com.hust.baseweb.applications.education.entity.EduCourse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EduCourseRepo extends JpaRepository<EduCourse, String> {

}
