package com.hust.openerp.taskmanagement.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.hust.openerp.taskmanagement.entity.MeetingPlan;

@Service
public interface PermissionService {
	public MeetingPlan checkMeetingPlanMember(String userId, UUID planId);
	public MeetingPlan checkMeetingPlanCreator(String userId, UUID planId);
}
