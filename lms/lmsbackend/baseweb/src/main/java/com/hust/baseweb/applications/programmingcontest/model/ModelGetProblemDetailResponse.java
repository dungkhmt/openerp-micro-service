package com.hust.baseweb.applications.programmingcontest.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ModelGetProblemDetailResponse implements Serializable {
    private String problemId;
    private String problemName;
    private String levelId;
    private int levelOrder;
    private String problemDescription;
    private String createdByUserId;
    private String submissionMode;
    private boolean unauthorized;
    private String problemRename;
    private String problemRecode;
}
