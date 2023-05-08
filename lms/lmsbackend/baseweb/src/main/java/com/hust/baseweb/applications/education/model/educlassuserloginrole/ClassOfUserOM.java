package com.hust.baseweb.applications.education.model.educlassuserloginrole;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClassOfUserOM {
    private UUID classId;
    private String classCode;
    private String courseId;
    private String courseName;
    private String semester;
    private String statusId;
    private String createdByUserLoginId;
}
