package com.hust.baseweb.applications.education.classmanagement.controller;

import java.security.Principal;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hust.baseweb.applications.education.entity.EduCourse;
import com.hust.baseweb.applications.education.model.AddCourseModel;
import com.hust.baseweb.applications.education.service.CourseService;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Log4j2
@Controller
@Validated
@RequestMapping("/edu/course")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CourseController {
    private CourseService courseService;

    @PostMapping("/create")
    public ResponseEntity<?> addEduClass(Principal principal, @RequestBody AddCourseModel addCourseModel) {
        log.info("addEduClass, start....");
        EduCourse eduCourse = courseService.createCourse(addCourseModel.getCourseId(), addCourseModel.getCourseName(), addCourseModel.getCredit());
        if (eduCourse != null) {
            return ResponseEntity.ok().body(eduCourse);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}
