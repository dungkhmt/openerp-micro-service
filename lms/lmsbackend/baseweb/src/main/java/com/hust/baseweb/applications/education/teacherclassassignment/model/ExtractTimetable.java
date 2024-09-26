package com.hust.baseweb.applications.education.teacherclassassignment.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class ExtractTimetable {

    private String[] start;

    private String[] end;

    private Set<Integer>[] weeks;
}
