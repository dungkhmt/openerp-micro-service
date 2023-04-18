package com.hust.baseweb.applications.programmingcontest.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ModelStudentRegisterContest {
    private String courseId;
    private String userId;
}
