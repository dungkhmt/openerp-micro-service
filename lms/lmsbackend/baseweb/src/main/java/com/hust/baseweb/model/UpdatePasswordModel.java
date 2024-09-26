package com.hust.baseweb.model;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter

public class UpdatePasswordModel {

    private String userLoginId;
    private String password;
}

