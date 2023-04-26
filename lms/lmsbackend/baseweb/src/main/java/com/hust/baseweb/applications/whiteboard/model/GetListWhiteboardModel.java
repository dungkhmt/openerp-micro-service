package com.hust.baseweb.applications.whiteboard.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetListWhiteboardModel {
    private String id;
    private String name;
    private Integer totalPage;
    private Date createdDate;
    private String createdBy;
}
