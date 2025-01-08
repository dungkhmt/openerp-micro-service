package com.hust.baseweb.applications.exam.model.request;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldNameConstants
public class ExamTestFilterReq {

    private String keyword;
    private String createdFrom;
    private String createdTo;
}
