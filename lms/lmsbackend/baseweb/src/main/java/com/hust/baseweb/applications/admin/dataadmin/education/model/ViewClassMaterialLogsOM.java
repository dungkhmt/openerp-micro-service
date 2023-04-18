package com.hust.baseweb.applications.admin.dataadmin.education.model;

import java.util.Date;

public interface ViewClassMaterialLogsOM {
    String getCourseId();
    String getCourseName();
    String getClassCode();
    String getSemester();
    String getChapterId();
    String getChapterName();
    String getMaterialName();
    Date getViewAt();
}
