package com.hust.baseweb.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class GetUserLoginOutputModel {

    private String userName;
    private UUID partyId;

    public GetUserLoginOutputModel() {
    }

    public GetUserLoginOutputModel(String userName, UUID partyId) {
        this.userName = userName;
        this.partyId = partyId;
    }
}
