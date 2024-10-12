package com.hust.baseweb.applications.programmingcontest.model;

import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class ModelContestSubmissionComment {

    private UUID id;
    private UUID submissionId;
    private String userId;
    private String comment;
    private Date createdStamp;
    private Date lastUpdatedStamp;
}
