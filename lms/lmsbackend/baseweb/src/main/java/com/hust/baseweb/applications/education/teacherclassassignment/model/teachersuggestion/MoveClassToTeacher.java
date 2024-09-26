package com.hust.baseweb.applications.education.teacherclassassignment.model.teachersuggestion;

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
public class MoveClassToTeacher {

    private String classCode;

    private List<TeacherCandidate> teachers;

    private String infoNewTeachers;
}
