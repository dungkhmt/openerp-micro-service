package com.hust.baseweb.applications.programmingcontest.model;

import java.util.Date;
import java.util.UUID;

public interface ContestMembers {

    UUID getId();

//    String getContestId();

    String getUserId();

    String getFirstName();

    String getLastName();

    String getRoleId();

    String getUpdatedByUserId();

    Date getLastUpdatedDate();

    String getPermissionId();

}
