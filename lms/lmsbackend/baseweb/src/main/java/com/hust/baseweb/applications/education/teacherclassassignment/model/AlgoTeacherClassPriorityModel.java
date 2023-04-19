package com.hust.baseweb.applications.education.teacherclassassignment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public
class AlgoTeacherClassPriorityModel {
    private int t;// teacher index
    private int c; // class index
    private int priority;
}
