package com.hust.baseweb.applications.programmingcontest.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ModelAddUserToContestResponse {

    private String userId;
    private String roleId;
    private String status;
    private String fullname;
}
