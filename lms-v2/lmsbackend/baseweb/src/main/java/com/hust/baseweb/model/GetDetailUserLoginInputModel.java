package com.hust.baseweb.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetDetailUserLoginInputModel {

    private String userName;

    public GetDetailUserLoginInputModel(String userName) {
        super();
        this.userName = userName;
    }

    public GetDetailUserLoginInputModel() {
        super();

    }

}
