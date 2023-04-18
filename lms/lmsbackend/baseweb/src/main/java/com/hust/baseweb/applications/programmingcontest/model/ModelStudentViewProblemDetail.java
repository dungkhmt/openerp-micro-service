package com.hust.baseweb.applications.programmingcontest.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModelStudentViewProblemDetail {
    private String problemName;
    private String problemCode;
    private String problemStatement;
    private String createdByUserLoginId;
    private String createdByUserFullName;
    private String submissionMode;
    private Date createdStamp;
    private List<byte[]> attachment;
    private List<String> attachmentNames;
}
