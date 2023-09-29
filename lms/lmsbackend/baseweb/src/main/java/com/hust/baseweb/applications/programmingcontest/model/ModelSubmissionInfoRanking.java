package com.hust.baseweb.applications.programmingcontest.model;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ModelSubmissionInfoRanking {

    private String problemId;
    private Long point;
    private Double pointPercentage;

    public ModelSubmissionInfoRanking(String problemId, Long point){
        this.problemId = problemId; this.point = point;
    }
}
