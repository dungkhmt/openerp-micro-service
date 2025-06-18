package com.hust.openerp.taskmanagement.service;

import java.util.Date;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.hust.openerp.taskmanagement.dto.MeetingPlanDTO;
import com.hust.openerp.taskmanagement.dto.form.CreateMeetingPlanForm;
import com.hust.openerp.taskmanagement.dto.form.UpdateMeetingPlanForm;
import com.hust.openerp.taskmanagement.entity.MeetingPlan;

@Service
public interface MeetingPlanService {
	Page<MeetingPlanDTO> getMeetingPlansByCreatorId(String userId, Pageable pageable, String q);
	
	Page<MeetingPlanDTO> getMeetingPlansByMemberId(String userId, Pageable pageable, String q);
	
	MeetingPlan getMeetingPlanById(String userId, UUID id);
	
	MeetingPlanDTO createMeetingPlan(String userId, CreateMeetingPlanForm form);

    void updateMeetingPlan(String userId, UUID planId, UpdateMeetingPlanForm form);
    
    void deleteMeetingPlan(String userId, UUID planId);
    
    void updateStatus(String userId, UUID planId, String statusId);
    
    void closeRegistrations(Date now);
    
    void startMeetingPlans(Date now);
    
    void completeMeetingPlans(Date now);
}
