package com.hust.baseweb.applications.exam.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@FieldNameConstants
@NoArgsConstructor
@AllArgsConstructor
public class MyExamFilterReq {

    private String keyword;
    private Integer status;
}
