package com.hust.openerp.taskmanagement.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.hust.openerp.taskmanagement.dto.MeetingSessionDTO;
import com.hust.openerp.taskmanagement.dto.form.BatchCreateSessionForm;

@Service
public interface MeetingSessionService {
	List<MeetingSessionDTO> getSessionsByPlanId(UUID planId, String userId);
	
	void createSessions(UUID planId, String userId, BatchCreateSessionForm createForm);
	
	void deleteSession(UUID planId, UUID sessionId, String userId);
}
