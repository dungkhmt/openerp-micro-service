package com.hust.openerp.taskmanagement.service.implement;

import com.hust.openerp.taskmanagement.entity.Group;
import com.hust.openerp.taskmanagement.entity.MeetingPlan;
import com.hust.openerp.taskmanagement.entity.Organization;
import com.hust.openerp.taskmanagement.exception.ApiException;
import com.hust.openerp.taskmanagement.exception.ErrorCode;
import com.hust.openerp.taskmanagement.repository.*;
import com.hust.openerp.taskmanagement.service.PermissionService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class PermissionServiceImplement implements PermissionService {

    private final MeetingPlanRepository meetingPlanRepository;
    private final MeetingPlanUserRepository meetingPlanUserRepository;
    private final OrganizationRepository organizationRepository;
    private final OrganizationUserRepository organizationUserRepository;
    private final GroupRepository groupRepository;
    private final GroupUserRepository groupUserRepository;

    @Override
    public MeetingPlan checkMeetingPlanCreatorOrMember(String userId, UUID planId) {
        MeetingPlan mp = meetingPlanRepository.findById(planId)
            .orElseThrow(() -> new ApiException(ErrorCode.MEETING_PLAN_NOT_FOUND));
        boolean isMember = meetingPlanUserRepository.existsByUserIdAndPlanId(userId, planId);
        boolean isCreator = mp.getCreatedBy().equals(userId);
        if (!isMember && !isCreator) {
            throw new ApiException(ErrorCode.NOT_A_MEMBER_OF_MEETING_PLAN);
        }
        return mp;
    }

    @Override
    public MeetingPlan checkMeetingPlanMember(String userId, UUID planId) {
        MeetingPlan mp = meetingPlanRepository.findById(planId)
            .orElseThrow(() -> new ApiException(ErrorCode.MEETING_PLAN_NOT_FOUND));
        if (!meetingPlanUserRepository.existsByUserIdAndPlanId(userId, planId)) {
            throw new ApiException(ErrorCode.NOT_A_MEMBER_OF_MEETING_PLAN);
        }
        return mp;
    }

    @Override
    public MeetingPlan checkMeetingPlanCreator(String userId, UUID planId) {
        MeetingPlan mp = meetingPlanRepository.findById(planId)
            .orElseThrow(() -> new ApiException(ErrorCode.MEETING_PLAN_NOT_FOUND));
        if (!mp.getCreatedBy().equals(userId)) {
            throw new ApiException(ErrorCode.INSUFFICIENT_PERMISSIONS);
        }
        return mp;
    }

    @Override
    public Organization checkOrganizationCreator(String userId, UUID orgId) {
        Organization org = organizationRepository.findById(orgId)
            .orElseThrow(() -> new ApiException(ErrorCode.ORGANIZATION_NOT_FOUND));
        if (!org.getCreatedBy().equals(userId)) {
            throw new ApiException(ErrorCode.INSUFFICIENT_PERMISSIONS);
        }
        return org;
    }

    @Override
    public Organization checkOrganizationMember(String userId, UUID orgId) {
        Organization org = organizationRepository.findById(orgId)
            .orElseThrow(() -> new ApiException(ErrorCode.ORGANIZATION_NOT_FOUND));
        if (!organizationUserRepository.existsByUserId(userId)) {
            throw new ApiException(ErrorCode.NOT_A_MEMBER_OF_ORGANIZATION);
        }
        return org;
    }

    @Override
    public Group checkGroupCreator(String userId, UUID groupId) {
        Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new ApiException(ErrorCode.GROUP_NOT_FOUND));
        if (!group.getCreatedBy().equals(userId)) {
            throw new ApiException(ErrorCode.INSUFFICIENT_PERMISSIONS);
        }
        return group;
    }

    @Override
    public Group checkGroupMember(String userId, UUID groupId) {
        Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new ApiException(ErrorCode.GROUP_NOT_FOUND));
        if (!groupUserRepository.existsByGroupIdAndUserIdAndThrsDateIsNull(groupId, userId)) {
            throw new ApiException(ErrorCode.NOT_A_MEMBER_OF_GROUP);
        }
        return group;
    }
}
