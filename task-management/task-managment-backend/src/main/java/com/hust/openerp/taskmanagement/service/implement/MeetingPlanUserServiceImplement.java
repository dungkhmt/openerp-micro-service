package com.hust.openerp.taskmanagement.service.implement;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hust.openerp.taskmanagement.algorithm.HopcroftKarpBinarySearch;
import com.hust.openerp.taskmanagement.dto.MeetingAutoAssignRequestDTO;
import com.hust.openerp.taskmanagement.dto.MeetingAutoAssignResponseDTO;
import com.hust.openerp.taskmanagement.dto.MeetingPlanUserDTO;
import com.hust.openerp.taskmanagement.dto.form.AddMeetingPlanUserForm;
import com.hust.openerp.taskmanagement.dto.form.UpdateMemberAssignmentsForm;
import com.hust.openerp.taskmanagement.entity.MeetingPlanUser;
import com.hust.openerp.taskmanagement.entity.MeetingPlanUser.MeetingPlanUserId;
import com.hust.openerp.taskmanagement.entity.User;
import com.hust.openerp.taskmanagement.exception.ApiException;
import com.hust.openerp.taskmanagement.exception.ErrorCode;
import com.hust.openerp.taskmanagement.repository.MeetingPlanUserRepository;
import com.hust.openerp.taskmanagement.repository.MeetingSessionUserRepository;
import com.hust.openerp.taskmanagement.service.MeetingPlanUserService;
import com.hust.openerp.taskmanagement.service.PermissionService;
import com.hust.openerp.taskmanagement.service.UserService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class MeetingPlanUserServiceImplement implements MeetingPlanUserService {

	private final MeetingPlanUserRepository meetingPlanUserRepository;
	private final MeetingSessionUserRepository meetingSessionUserRepository;
	private final UserService userService;
	private final PermissionService permissionService;
	private final ModelMapper modelMapper;

	@Override
	public List<User> getAllMeetingPlanUsers(String userId, UUID planId) {
		permissionService.checkMeetingPlanMember(userId, planId);
		return meetingPlanUserRepository.findUsersByPlanId(planId);
	}

	@Override
	public void addMeetingPlanUser(String userId, AddMeetingPlanUserForm addForm) {
		UUID planId = addForm.getPlanId();
		permissionService.checkMeetingPlanCreator(userId, planId);

		List<String> memberIds = addForm.getUserId();
		for (String memberId : memberIds) {
			User member = userService.findById(memberId);
			if (member == null) {
				throw new ApiException(ErrorCode.USER_NOT_EXIST);
			}
			MeetingPlanUser meetingPlanUser = MeetingPlanUser.builder().planId(planId).userId(memberId).build();
			meetingPlanUser = meetingPlanUserRepository.save(meetingPlanUser);
		}
	}

	@Override
	@Transactional
	public void removeMeetingPlanUser(String userId, UUID planId, String memberId) {
		permissionService.checkMeetingPlanCreator(userId, planId);

		meetingSessionUserRepository.deleteByUserId(userId);

		MeetingPlanUserId compositeKey = new MeetingPlanUserId(planId, memberId);
		if (!meetingPlanUserRepository.existsById(compositeKey)) {
			throw new ApiException(ErrorCode.MEETING_PLAN_USER_NOT_EXIST);
		}
		meetingPlanUserRepository.deleteById(compositeKey);
	}

	@Override
	public MeetingPlanUserDTO getMyAssignment(String userId, UUID planId) {
		permissionService.checkMeetingPlanMember(userId, planId);

		MeetingPlanUser res = meetingPlanUserRepository.findByPlanIdAndUserId(planId, userId);
		return modelMapper.map(res, MeetingPlanUserDTO.class);
	}

	@Override
	public List<MeetingPlanUserDTO> getMemberAssignments(String userId, UUID planId) {
		permissionService.checkMeetingPlanCreator(userId, planId);
		
		List<MeetingPlanUser> res = meetingPlanUserRepository.findByPlanId(planId);
		return res.stream().map(plan -> modelMapper.map(plan, MeetingPlanUserDTO.class)).collect(Collectors.toList());
	}

	@Override
	public void updateMemberAssignment(String userId, UUID planId, UpdateMemberAssignmentsForm assignments) {
		permissionService.checkMeetingPlanCreator(userId, planId);
		
		for (MeetingPlanUserDTO assignment : assignments.getAssignments()) {
			MeetingPlanUserId compositeKey = new MeetingPlanUserId(planId, assignment.getUserId());
			MeetingPlanUser member = meetingPlanUserRepository.findById(compositeKey)
					.orElseThrow(() -> new ApiException(ErrorCode.MEETING_PLAN_USER_NOT_EXIST));
			member.setSessionId(assignment.getSessionId());
			meetingPlanUserRepository.save(member);
		}
	}

	@Override
	public MeetingAutoAssignResponseDTO autoAssignMembers(String userId, UUID planId,
			MeetingAutoAssignRequestDTO request) {
		permissionService.checkMeetingPlanCreator(userId, planId);

		// Use HopcroftKarpBinarySearch to compute the assignment
		HopcroftKarpBinarySearch scheduler = new HopcroftKarpBinarySearch(request.getMemberPreferences());
		int minExtraCapacity = scheduler.findMinExtraCapacity();
		
		Map<String, UUID> assignment = scheduler.getAssignment();
		int minimalMaxLoad = 1 + minExtraCapacity;
		return new MeetingAutoAssignResponseDTO(assignment, minimalMaxLoad);
	}
}