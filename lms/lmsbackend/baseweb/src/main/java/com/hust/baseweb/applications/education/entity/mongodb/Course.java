package com.hust.baseweb.applications.education.entity.mongodb;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;


@Getter
@Setter
@Document(collection = "course")
public class Course {

    @Id
    private String courseId;

    private String courseName;

    private int credit;

    @Transient
    private int __rowNum__;
}
