package com.hust.baseweb.applications.education.model;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseSessionInteractiveQuizCreateModel {
    
    private String interactiveQuizName;
    private UUID sessionId;
    private String description;
}
