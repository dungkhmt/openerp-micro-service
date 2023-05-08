package com.hust.baseweb.applications.education.teacherclassassignment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Currently OK. Can dung de mo hinh khoa hoc ma giang vien co the day khi chay thuat toan.
 */
@Getter
@Setter
@AllArgsConstructor
public class Course4Teacher {

    private String id;

    private String name;

    private int priority;

    private String classType;
}
