package com.hust.baseweb.applications.examclassandaccount.model;

import com.hust.baseweb.applications.examclassandaccount.entity.ExamClassUserloginMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModelRepsonseExamClassDetail {
    private UUID examClassId;
    private String name;
    private String description;
    private String executeDate;
    private String status;
    private List<String> statusList;
    private List<ExamClassUserloginMap> accounts;
}
