package com.hust.baseweb.applications.admin.dataadmin.model.education;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LogUserLoginCourseChapterMaterialOutputModel {
    private String userLoginId;
    private String fullname;
    private String affiliations;
    private String classId;
    private String courseId;
    private String courseName;
    private String chapterId;
    private String chapterName;
    private String materialName;
    private Date date;
}
