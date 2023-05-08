package com.hust.baseweb.applications.education.model;

import java.util.Date;

public interface GetAssigns4TeacherOM extends GetAssigns4StudentOM {

    Date getOpenTime();

    boolean getDeleted();
}
