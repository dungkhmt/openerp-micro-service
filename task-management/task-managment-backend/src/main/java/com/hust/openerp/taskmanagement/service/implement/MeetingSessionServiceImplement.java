package com.hust.openerp.taskmanagement.service.implement;

import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hust.openerp.taskmanagement.dto.MeetingSessionDTO;
import com.hust.openerp.taskmanagement.dto.form.BatchCreateSessionForm;
import com.hust.openerp.taskmanagement.dto.form.CreateMeetingSessionForm;
import com.hust.openerp.taskmanagement.entity.MeetingPlanUser;
import com.hust.openerp.taskmanagement.entity.MeetingSession;
import com.hust.openerp.taskmanagement.repository.MeetingPlanUserRepository;
import com.hust.openerp.taskmanagement.repository.MeetingSessionRepository;
import com.hust.openerp.taskmanagement.repository.MeetingSessionUserRepository;
import com.hust.openerp.taskmanagement.service.MeetingSessionService;
import com.hust.openerp.taskmanagement.service.PermissionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MeetingSessionServiceImplement implements MeetingSessionService {
	
	private final MeetingSessionRepository meetingSessionRepository;
	private final MeetingSessionUserRepository meetingSessionUserRepository;
	private final MeetingPlanUserRepository meetingPlanUserRepository;
	private final PermissionService permissionService;
	private final ModelMapper modelMapper;

	@Override
	public List<MeetingSessionDTO> getSessionsByPlanId(UUID planId, String userId) {
		permissionService.checkMeetingPlanCreatorOrMember(userId, planId);
		return meetingSessionRepository.findByPlanIdOrderByStartTimeAsc(planId).stream()
				.map(session -> modelMapper.map(session, MeetingSessionDTO.class)).toList();
	}

	@Override
	@Transactional
	public void createSessions(UUID planId, String userId, BatchCreateSessionForm createForm) {
		permissionService.checkMeetingPlanCreator(userId, planId);
		
		for (CreateMeetingSessionForm sessionForm : createForm.getSessions()) {
			var session = modelMapper.map(sessionForm, MeetingSession.class);
			session.setId(UUID.randomUUID());
			session.setPlanId(planId);
			session = meetingSessionRepository.save(session);
		}
	}

	@Override
	@Transactional
	public void deleteSession(UUID planId, UUID sessionId, String userId) {
		permissionService.checkMeetingPlanCreator(userId, planId);
		
		List<MeetingPlanUser> planUsers = meetingPlanUserRepository.findByMeetingPlanIdAndSessionId(planId, sessionId);
	    for (MeetingPlanUser planUser : planUsers) {
	        planUser.setSessionId(null);
	    }
	    meetingPlanUserRepository.saveAll(planUsers);	    
	    meetingSessionUserRepository.deleteBySessionId(sessionId);	    
		meetingSessionRepository.deleteById(sessionId);
	}

}
