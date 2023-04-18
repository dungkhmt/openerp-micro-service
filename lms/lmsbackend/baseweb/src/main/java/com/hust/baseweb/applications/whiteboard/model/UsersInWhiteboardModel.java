package com.hust.baseweb.applications.whiteboard.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.parameters.P;

@Getter
@Setter
@AllArgsConstructor
public class UsersInWhiteboardModel {
    private String userId;
    private String roleId;
    private String statusId;
    private Boolean isCreatedUser;

    public UsersInWhiteboardModel() {

    }
}
