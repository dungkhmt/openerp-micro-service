package com.hust.baseweb.applications.exam.model.request;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@FieldNameConstants
public class ExamFilterReq {

    private String keyword;
    private Integer status;
    private String startTimeFrom;
    private String startTimeTo;
    private String endTimeFrom;
    private String endTimeTo;
}
