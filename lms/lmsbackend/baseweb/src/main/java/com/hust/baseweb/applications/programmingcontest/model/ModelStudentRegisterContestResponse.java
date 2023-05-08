package com.hust.baseweb.applications.programmingcontest.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ModelStudentRegisterContestResponse {
    private String status;
    private String message;
}
