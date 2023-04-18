package com.hust.baseweb.applications.programmingcontest.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModelMemberOfContestResponse {
    private UUID id;
    private String contestId;
    private String userId;
    private String fullName;
    private String roleId;
    private String updatedByUserId;
    private Date lastUpdatedDate;
    private String permissionId;
    private Date date;
}
