package com.hust.baseweb.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordChangeModel {

    private String currentPassword;
    private String newPassword;

    public PasswordChangeModel() {
    }
}
