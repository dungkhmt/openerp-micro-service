package com.hust.baseweb.applications.education.teacherclassassignment.model.teachersuggestion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TeacherCandidate{
    private String teacherId;
    private int numberClasses;
    private int numberDays;
    private double hourLoad;// GD
}
