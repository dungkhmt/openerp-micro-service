package com.hust.baseweb.applications.education.teacherclassassignment.entity.compositeid;

import com.hust.baseweb.applications.education.teacherclassassignment.entity.TeacherForAssignmentPlan;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

/**
 * Id for {@link TeacherForAssignmentPlan}.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class TeacherPlanId implements Serializable {

    private String teacherId;

    private UUID planId;
}
