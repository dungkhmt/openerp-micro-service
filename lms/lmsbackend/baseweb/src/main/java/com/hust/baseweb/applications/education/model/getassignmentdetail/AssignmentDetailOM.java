package com.hust.baseweb.applications.education.model.getassignmentdetail;

import java.util.Date;

public interface AssignmentDetailOM {

    String getName();

    String getSubject();

    Date getOpenTime();

    Date getCloseTime();

    boolean getDeleted();
}
