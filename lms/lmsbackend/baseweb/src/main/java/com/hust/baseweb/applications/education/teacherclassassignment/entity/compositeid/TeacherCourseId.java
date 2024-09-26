package com.hust.baseweb.applications.education.teacherclassassignment.entity.compositeid;

import com.hust.baseweb.applications.education.teacherclassassignment.entity.TeacherCourse;
import lombok.*;

import java.io.Serializable;

/**
 * Id for {@link TeacherCourse}.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class TeacherCourseId implements Serializable {

    private String teacherId;

    private String courseId;

    private String classType;
}
