package com.hust.baseweb.applications.education.teacherclassassignment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * OK
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClassesAssignedToATeacherModel {

    private String teacherId;

    private String teacherName;

    private double hourLoad;

    private int numberOfClass;

    private int numberOfWorkingDays;

    private List<ClassTeacherAssignmentSolutionModel> classList; // SORTED

    private int remainEmptySlots; // so tiet trong ke tu tiet cuoi cung cua lop cuoi cung den het tiet 12 cua thu 6
}
