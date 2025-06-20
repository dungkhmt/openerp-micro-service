package com.hust.openerp.taskmanagement.service;

import com.hust.openerp.taskmanagement.entity.Group;
import com.hust.openerp.taskmanagement.entity.MeetingPlan;
import com.hust.openerp.taskmanagement.entity.Organization;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface PermissionService {
    public MeetingPlan checkMeetingPlanMember(String userId, UUID planId);

    public MeetingPlan checkMeetingPlanCreator(String userId, UUID planId);

    public MeetingPlan checkMeetingPlanCreatorOrMember(String userId, UUID planId);

    public Organization checkOrganizationCreator(String userId, UUID orgId);

    public Organization checkOrganizationMember(String userId, UUID orgId);

    public Group checkGroupCreator(String userId, UUID groupId);

    public Group checkGroupMember(String userId, UUID groupId);
}
