package com.hust.baseweb.applications.exam.model.request;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@FieldNameConstants
public class ExamQuestionSaveReq {

    private String code;
    private Integer type;
    private String content;
    private String filePath;
    private Integer numberAnswer;
    private String contentAnswer1;
    private String contentAnswer2;
    private String contentAnswer3;
    private String contentAnswer4;
    private String contentAnswer5;
    private boolean multichoice;
    private String answer;
    private String explain;
    private List<String> deletePaths;
}
