package com.hust.baseweb.applications.admin.dataadmin.model.education;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModelResponseProblemAndSolutionCode {
    private String problemId;
    private String problemName;
    private String problemStatement;
    private List<String> solutionCodes;

}
