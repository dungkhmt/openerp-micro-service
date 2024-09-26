package com.hust.baseweb.applications.education.teacherclassassignment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Chua xem xet
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TeacherClassAssignmentOM {

    private TeacherClassAssignmentModel[] assignments;

    private ClassesAssigned2TeacherModel[] classesAssigned2TeacherModels;

    private List<AlgoClassIM> notAssignedClasses = new ArrayList<>();
}

