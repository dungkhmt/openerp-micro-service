package com.hust.baseweb.applications.programmingcontest.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ModelAddUserToContestGroupResponse {
    private String userId;
    private String participantId;
    private String status;
}
