package com.hust.baseweb.applications.programmingcontest.model;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ContestSubmissionsByUser {

    private List<ModelSubmissionInfoRanking> mapProblemsToPoints;
    private String userId;
    private String fullname;
    private long totalPoint;
    private double totalPercentagePoint;
    private String stringTotalPercentagePoint;
}
