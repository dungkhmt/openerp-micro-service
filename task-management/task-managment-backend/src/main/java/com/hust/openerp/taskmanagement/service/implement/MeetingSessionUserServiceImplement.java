package com.hust.openerp.taskmanagement.service.implement;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hust.openerp.taskmanagement.dto.MeetingSessionDTO;
import com.hust.openerp.taskmanagement.dto.MeetingSessionUserDTO;
import com.hust.openerp.taskmanagement.dto.form.UpdateMemberRegistrationsForm;
import com.hust.openerp.taskmanagement.entity.MeetingPlan;
import com.hust.openerp.taskmanagement.entity.MeetingSessionUser;
import com.hust.openerp.taskmanagement.exception.ApiException;
import com.hust.openerp.taskmanagement.exception.ErrorCode;
import com.hust.openerp.taskmanagement.repository.MeetingSessionUserRepository;
import com.hust.openerp.taskmanagement.service.MeetingSessionUserService;
import com.hust.openerp.taskmanagement.service.PermissionService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class MeetingSessionUserServiceImplement implements MeetingSessionUserService {

	private final MeetingSessionUserRepository meetingSessionUserRepository;
	private final PermissionService permissionService;
	private final ModelMapper modelMapper;

	@Override
	public List<MeetingSessionDTO> getSessionsByMe(UUID planId, String userId) {
		permissionService.checkMeetingPlanMember(userId, planId);
		return meetingSessionUserRepository.findSessionsByUserIdAndPlanId(userId, planId).stream()
				.map(session -> modelMapper.map(session, MeetingSessionDTO.class)).toList();
	}
	
	@Override
	public List<MeetingSessionUserDTO> getAllSessionRegistrations( UUID planId, String userId) {
		permissionService.checkMeetingPlanCreator(userId, planId);
		return meetingSessionUserRepository.findAllSessionRegistration(planId).stream()
				.map(session -> modelMapper.map(session, MeetingSessionUserDTO.class)).toList();
	}

	@Override
	@Transactional
	public void updateMyMeetingSessions(UUID planId, String userId, UpdateMemberRegistrationsForm form) {
		MeetingPlan mPlan = permissionService.checkMeetingPlanMember(userId, planId);
		if (mPlan.getRegistrationDeadline() != null && new Date().after(mPlan.getRegistrationDeadline())) {
			 throw new ApiException(ErrorCode.REGISTRATION_CLOSED);
	    }

		meetingSessionUserRepository.deleteByUserIdAndPlanId(userId, planId);
		for (UUID sessionId : form.getSessionIds()) {
			MeetingSessionUser tmp = MeetingSessionUser.builder().sessionId(sessionId).userId(userId).build();
			meetingSessionUserRepository.save(tmp);
		}
	}

}