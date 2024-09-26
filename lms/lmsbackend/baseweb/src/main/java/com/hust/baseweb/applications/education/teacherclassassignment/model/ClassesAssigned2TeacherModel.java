package com.hust.baseweb.applications.education.teacherclassassignment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Chua xem xet nhung co ve on
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClassesAssigned2TeacherModel {

    private AlgoTeacherIM teacher;

    private List<AlgoClassIM> classes;
}
