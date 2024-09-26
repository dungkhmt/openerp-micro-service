package com.hust.baseweb.applications.education.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EduCourseSessionModelCreate {
    
    private String courseId;
    private String sessionName;
    private String description;
}
