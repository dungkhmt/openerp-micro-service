package com.hust.baseweb.applications.education.classmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModelResponseEduClassDetail {

    private UUID id;

    private String classCode;

    private String statusId;

    private String semesterId;

    private String courseId;
    private String courseName;

    private String classType;

    private String createdByUserId;

    private Date createdStamp;

}
