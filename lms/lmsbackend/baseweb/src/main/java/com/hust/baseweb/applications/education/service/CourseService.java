package com.hust.baseweb.applications.education.service;


import com.hust.baseweb.applications.education.entity.EduCourse;

import java.util.List;

public interface CourseService {
    EduCourse createCourse(String Id, String courseName, short credit);
    List<EduCourse> findAll();
}
