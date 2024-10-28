package com.hust.baseweb.applications.programmingcontest.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.Pattern;

@Data
public class ModelCreateContest {

    @Pattern(regexp = "^[^%^\\/\\|. ?;\\[\\]]*?$", message = "Invalid contest id. Contest id must not contain special characters include %^/\\|.?;[]")
    private String contestId;
    private String contestName;
    private long contestTime;
    private int maxNumberSubmissions;
    private List<String> problemIds;
    private Date startedAt;
    private long countDownTime;
    private int maxSourceCodeLength;
    private int minTimeBetweenTwoSubmissions;
    private String judgeMode;
    private String contestType;
}
