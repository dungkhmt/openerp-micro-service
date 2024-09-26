package com.hust.baseweb.applications.programmingcontest.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ModelProblemGeneralInfo {

    private String problemId;
    private String problemName;
    private String levelId;
    private String problemDescription;

}
