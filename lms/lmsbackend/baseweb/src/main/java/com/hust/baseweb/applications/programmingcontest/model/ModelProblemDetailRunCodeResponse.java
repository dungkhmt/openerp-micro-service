package com.hust.baseweb.applications.programmingcontest.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ModelProblemDetailRunCodeResponse {
    String output;
    String expected;
    String status;
}
