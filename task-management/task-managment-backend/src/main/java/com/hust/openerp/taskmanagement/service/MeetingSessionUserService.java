package com.hust.openerp.taskmanagement.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.hust.openerp.taskmanagement.dto.MeetingSessionDTO;
import com.hust.openerp.taskmanagement.dto.MeetingSessionUserDTO;
import com.hust.openerp.taskmanagement.dto.form.UpdateMemberRegistrationsForm;

@Service
public interface MeetingSessionUserService {
	List<MeetingSessionDTO> getSessionsByMe(UUID planId, String userId);
	
	List<MeetingSessionUserDTO> getAllSessionRegistrations( UUID planId, String userId);
	
	void updateMyMeetingSessions(UUID planId, String userId, UpdateMemberRegistrationsForm form);
}
