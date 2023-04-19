package com.hust.baseweb.applications.programmingcontest.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ModelAddProblemLanguageSourceCode {
    private String problemSourceCodeId;
    private String baseSource;
    private String mainSource;
    private String problemFunctionDefaultSource;
    private String problemFunctionSolution;
    private String language;

}
