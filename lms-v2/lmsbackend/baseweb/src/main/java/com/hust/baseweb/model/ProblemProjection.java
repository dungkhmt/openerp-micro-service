package com.hust.baseweb.model;

import java.util.Date;

public interface ProblemProjection {

    String getProblemId();

    String getProblemName();

    String getUserId();

    String getLevelId();

    Date getCreatedAt();

    int getAppearances();

    String getJsonTags();

    String getStatusId();

}
