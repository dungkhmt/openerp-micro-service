package com.hust.baseweb.applications.programmingcontest.model;

import java.util.UUID;

public interface TestCaseDetailProjection {

    String getTestCase();

    String getCorrectAns();

    int getPoint();

    UUID getTestCaseId();

    String getIsPublic();

    String getStatus();

    String getDescription();
}
