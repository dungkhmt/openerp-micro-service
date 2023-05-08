package com.hust.baseweb.applications.whiteboard.service;

import com.hust.baseweb.applications.whiteboard.model.*;
import com.hust.baseweb.entity.UserLogin;

import java.util.List;
import java.util.UUID;

public interface WhiteboardService {
    void createWhiteboard(String userId, String whiteboardId, String name, UUID classSessionId);
    List<GetListWhiteboardModel> getWhiteboards(UUID sessionId);

    void saveWhiteboardData(SaveWhiteboardDataModel input, String userId);

    WhiteboardDetailModel getWhiteboardDetail(String id);

    AddUserToWhiteboardResultModel addUserToWhiteboard(String whiteboardId, UserLogin userLogin, AddUserToWhiteboardModel input);

    GetUserWhiteboardModel getUserWhiteboard(String whiteboardId, UserLogin userLogin);

    ChangeRoleStatusModel changeRoleStatusUserWhiteboard(String whiteboardId, ChangeRoleStatusModel input);

    ListDrawRequestPendingModel getListDrawRequestPending(String whiteboardId, UserLogin userLogin);

    List<UsersInWhiteboardModel> getListUsersInWhiteboard(String whiteboardId);

    DeleteWhiteboardResultModel deleteWhiteboard(String userId, DeleteWhiteboardModel input);
}
