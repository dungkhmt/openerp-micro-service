package com.hust.baseweb.applications.education.teacherclassassignment.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ExtractDataOM {

    private List<AlgoTeacherIM> teachers;

    private List<AlgoClassIM> classes;
}
