package com.hust.baseweb.applications.whiteboard.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetUserWhiteboardModel {
    private String roleId;
    private String statusId;
}
