package com.hust.baseweb.applications.education.entity.mongodb;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "teacher")
public class Teacher {

    @Id
    private String email;

    private String teacherName;

    private int maxCredit;

    @Transient
    private int __rowNum__;
}
