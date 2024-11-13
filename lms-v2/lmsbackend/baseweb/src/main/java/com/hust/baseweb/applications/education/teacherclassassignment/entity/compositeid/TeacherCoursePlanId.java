package com.hust.baseweb.applications.education.teacherclassassignment.entity.compositeid;

import com.hust.baseweb.applications.education.teacherclassassignment.entity.TeacherCourseForAssignmentPlan;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

/**
 * Id for {@link TeacherCourseForAssignmentPlan}.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class TeacherCoursePlanId implements Serializable {

    private UUID teacherCourseId;

    private UUID planId;

}
