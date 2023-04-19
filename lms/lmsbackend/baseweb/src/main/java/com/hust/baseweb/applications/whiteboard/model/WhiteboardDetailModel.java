package com.hust.baseweb.applications.whiteboard.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WhiteboardDetailModel {
    private String id;
    private String name;
    private String type;
    private String data;
    private Integer totalPage;
}
