package com.hust.baseweb.applications.education.teacherclassassignment.service;

import com.hust.baseweb.applications.education.teacherclassassignment.model.AlgoTeacherAssignmentIM;
import com.hust.baseweb.applications.education.teacherclassassignment.model.TeacherClassAssignmentOM;


public interface TeacherClassAssignmentAlgoService {

    TeacherClassAssignmentOM computeTeacherClassAssignment(AlgoTeacherAssignmentIM input);
}
