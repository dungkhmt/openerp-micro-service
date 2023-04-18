package com.hust.baseweb.applications.programmingcontest.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CodeSimilarityElement {
    private String source1;
    private String userLoginId1;
    private Date submitDate1;
    private String problemId1;

    private String source2;
    private String userLoginId2;
    private Date submitDate2;
    private String problemId2;

    private double score;

}
