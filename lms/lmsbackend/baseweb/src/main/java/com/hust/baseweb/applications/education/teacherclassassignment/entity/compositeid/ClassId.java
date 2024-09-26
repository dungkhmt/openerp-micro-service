package com.hust.baseweb.applications.education.teacherclassassignment.entity.compositeid;

import com.hust.baseweb.applications.education.teacherclassassignment.entity.ClassTeacherAssignmentClassInfo;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

/**
 * Id for {@link ClassTeacherAssignmentClassInfo}.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ClassId implements Serializable {

    private String classId;

    private UUID planId;
}
