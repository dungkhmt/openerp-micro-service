package com.hust.baseweb.applications.programmingcontest.model;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class ModelGetContestResponse {

    private String contestId;
    private String contestName;
    private long contestTime;
    private long countDown;
    private Date startAt;
    private String statusId;
    private String userId;
    private String roleId;
    private String registrationStatusId;
    private Date createdAt;
    private Boolean contestPublic;
}
