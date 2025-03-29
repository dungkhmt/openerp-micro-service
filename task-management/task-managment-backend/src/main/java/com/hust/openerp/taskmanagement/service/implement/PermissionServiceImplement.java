package com.hust.openerp.taskmanagement.service.implement;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hust.openerp.taskmanagement.entity.MeetingPlan;
import com.hust.openerp.taskmanagement.exception.ApiException;
import com.hust.openerp.taskmanagement.exception.ErrorCode;
import com.hust.openerp.taskmanagement.repository.MeetingPlanRepository;
import com.hust.openerp.taskmanagement.repository.MeetingPlanUserRepository;
import com.hust.openerp.taskmanagement.service.PermissionService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class PermissionServiceImplement implements PermissionService {

	private final MeetingPlanRepository meetingPlanRepository;
	private final MeetingPlanUserRepository meetingPlanUserRepository;

	@Override
	public MeetingPlan checkMeetingPlanMember(String userId, UUID planId) {
		MeetingPlan mp = meetingPlanRepository.findById(planId)
				.orElseThrow(() -> new ApiException(ErrorCode.MEETING_PLAN_NOT_EXIST));
		if (!meetingPlanUserRepository.existsByUserIdAndPlanId(userId, planId) 
				&& !mp.getCreatedBy().equals(userId)) {
			throw new ApiException(ErrorCode.NOT_A_MEMBER_OF_MEETING_PLAN);
		}
		return mp;
	}

	@Override
	public MeetingPlan checkMeetingPlanCreator(String userId, UUID planId) {
		MeetingPlan mp = meetingPlanRepository.findById(planId)
				.orElseThrow(() -> new ApiException(ErrorCode.MEETING_PLAN_NOT_EXIST));
		if (!mp.getCreatedBy().equals(userId)) {
			throw new ApiException(ErrorCode.INSUFFICIENT_PERMISSIONS);
		}
		return mp;
	}

}
