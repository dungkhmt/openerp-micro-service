package com.hust.baseweb.applications.programmingcontest.model;

import lombok.Data;

@Data
public class ModelProblemInfoInContest {
    private String contestId;
    private String problemId;
    private String problemName;
    private String problemRename;
    private String problemRecode;
    private String submissionMode;

}
