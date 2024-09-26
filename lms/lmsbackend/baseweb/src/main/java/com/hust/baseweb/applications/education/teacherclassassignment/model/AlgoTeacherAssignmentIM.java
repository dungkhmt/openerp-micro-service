package com.hust.baseweb.applications.education.teacherclassassignment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * OK. Du lieu da duoc xu ly va chuyen doi thanh dau vao thuat toan
 */
@Getter
@Setter
@AllArgsConstructor
public class AlgoTeacherAssignmentIM {

    private AlgoTeacherIM[] teachers; // Only teacher in the plan

    private AlgoClassIM[] classes; // All classes in plan

    private TeacherClassAssignmentModel[] preAssignments; // Some classes are preassigned

    private SolverConfig config;
}
