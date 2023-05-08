package com.hust.baseweb.applications.programmingcontest.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModelGetRolesOfUserInContestResponse {
    private String userId;
    private String contestId;
    private List<String> rolesApprovedInContest;
    private List<String> rolesNotInContest;
}
