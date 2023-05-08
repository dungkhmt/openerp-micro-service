package com.hust.baseweb.applications.education.teacherclassassignment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Mo hinh giai phap bai toan, lop duoc phan cho giao vien
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TeacherClassAssignmentModel {

    private AlgoClassIM algoClassIM;

    private AlgoTeacherIM algoTeacherIM;
}
