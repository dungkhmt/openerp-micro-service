package com.hust.baseweb.applications.whiteboard.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChangeRoleStatusModel {
    private String userId;
    private String roleId;
    private String statusId;

    public ChangeRoleStatusModel(){

    }
}
