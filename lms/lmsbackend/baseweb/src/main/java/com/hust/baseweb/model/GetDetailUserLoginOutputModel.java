package com.hust.baseweb.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetDetailUserLoginOutputModel {

    private String userName;
    private String partyId;
    private SecurityGroupOutputModel[] allSecurityGroups;

    public GetDetailUserLoginOutputModel() {
        super();

    }

    public GetDetailUserLoginOutputModel(
        String userName, String partyId,
        SecurityGroupOutputModel[] allSecurityGroups
    ) {
        super();
        this.userName = userName;
        this.partyId = partyId;
        this.allSecurityGroups = allSecurityGroups;
    }

}
