package com.hust.openerp.taskmanagement.service;

import com.hust.openerp.taskmanagement.dto.GroupDTO;
import com.hust.openerp.taskmanagement.dto.form.GroupForm;

import java.util.List;
import java.util.UUID;

public interface GroupService {
    GroupDTO getGroupById(String currentUserId, UUID id);

    GroupDTO createGroup(String currentUserId, GroupForm request);

    GroupDTO updateGroup(String currentUserId, UUID id, GroupForm request);

    List<GroupDTO> getGroupsByUserId(String userId);
}
