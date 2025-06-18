package com.hust.openerp.taskmanagement.service;

import com.hust.openerp.taskmanagement.dto.GroupUserDTO;
import com.hust.openerp.taskmanagement.dto.form.GroupUserForm;

import java.util.List;
import java.util.UUID;

public interface GroupUserService {
    List<GroupUserDTO> getUsersByGroupId(String currentUserId, UUID groupId);

    void addUserToGroup(String currentUserId, UUID groupId, GroupUserForm request);

    void removeUserFromGroup(String currentUserId, UUID groupId, String userId);
}
