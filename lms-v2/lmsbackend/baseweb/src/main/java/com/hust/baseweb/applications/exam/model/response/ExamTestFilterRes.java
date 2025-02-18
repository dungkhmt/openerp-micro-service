package com.hust.baseweb.applications.exam.model.response;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@FieldNameConstants
public class ExamTestFilterRes {

    private String id;
    private String code;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ExamTestQuestionFilterRes> examTestQuestionFilterResList;
}
