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
public class ModelContestByRoleResponse {
    private String contestId;
    private String roleId;
    private String createdByUserLoginId;
    private Date createdDate;
}
