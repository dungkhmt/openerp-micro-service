package com.hust.baseweb.applications.admin.dataadmin.education.model;

import java.util.Date;
import java.util.UUID;

public interface ProgrammingContestSubmissionOM {
    String getContestId();
    String getContestName();
    String getProblemId();
    String getProblemName();
    String getSubmissionId();
    String getStatus();
    String getTestCasePass();
    Integer getPoint();
    String getSourceCodeLanguage();
    Date getSubmitAt();
}
