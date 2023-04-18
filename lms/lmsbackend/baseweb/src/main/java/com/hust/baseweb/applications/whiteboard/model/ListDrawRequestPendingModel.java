package com.hust.baseweb.applications.whiteboard.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ListDrawRequestPendingModel {
    private List<AddUserToWhiteboardResultModel> addUserToWhiteboardResultModelList;

    public ListDrawRequestPendingModel() {

    }
}
