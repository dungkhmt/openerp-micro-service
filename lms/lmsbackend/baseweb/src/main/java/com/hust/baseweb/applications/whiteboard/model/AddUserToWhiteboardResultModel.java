package com.hust.baseweb.applications.whiteboard.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AddUserToWhiteboardResultModel {
    private String roleId;
    private String statusId;
    private String userId;
    private Boolean isCreatedUser;

    public AddUserToWhiteboardResultModel() {

    }
}
