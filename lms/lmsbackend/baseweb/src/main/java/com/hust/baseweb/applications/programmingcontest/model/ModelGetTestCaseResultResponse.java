package com.hust.baseweb.applications.programmingcontest.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
public class ModelGetTestCaseResultResponse {
    private String result;
    private String status;
}
