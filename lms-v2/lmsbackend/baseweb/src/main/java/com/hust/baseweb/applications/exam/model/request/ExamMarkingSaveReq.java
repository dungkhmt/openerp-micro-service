package com.hust.baseweb.applications.exam.model.request;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.util.List;

@Getter
@Setter
@FieldNameConstants
public class ExamMarkingSaveReq {

    private String examResultId;
    private String comment;
    private Integer totalScore;
    private List<ExamMarkingDetailsSaveReq> examResultDetails;
}
