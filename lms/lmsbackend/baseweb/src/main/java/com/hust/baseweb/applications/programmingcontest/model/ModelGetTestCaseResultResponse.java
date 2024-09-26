package com.hust.baseweb.applications.programmingcontest.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ModelGetTestCaseResultResponse {

    private String result;
    private String status;
}
