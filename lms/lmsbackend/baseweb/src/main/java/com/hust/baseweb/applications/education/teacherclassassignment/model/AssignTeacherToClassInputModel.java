package com.hust.baseweb.applications.education.teacherclassassignment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * OK
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AssignTeacherToClassInputModel {

    private UUID planId;

    private String classId;

    private String teacherId;

    private boolean pinned;
}
