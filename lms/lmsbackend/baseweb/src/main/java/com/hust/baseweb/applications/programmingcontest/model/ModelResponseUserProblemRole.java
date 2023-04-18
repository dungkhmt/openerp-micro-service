package com.hust.baseweb.applications.programmingcontest.model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModelResponseUserProblemRole {
    private String userLoginId;
    private String problemId;
    private String roleId;
}
