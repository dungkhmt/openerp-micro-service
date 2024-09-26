package com.hust.baseweb.applications.examclassandaccount.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModelCreateExamClass {
    private String name;
    private String description;
    private String executeDate;
}
