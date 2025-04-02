package com.hust.openerp.taskmanagement.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.hust.openerp.taskmanagement.dto.MeetingAutoAssignRequestDTO;
import com.hust.openerp.taskmanagement.dto.MeetingAutoAssignResponseDTO;
import com.hust.openerp.taskmanagement.dto.MeetingPlanUserDTO;
import com.hust.openerp.taskmanagement.dto.form.AddMeetingPlanUserForm;
import com.hust.openerp.taskmanagement.dto.form.UpdateMemberAssignmentsForm;
import com.hust.openerp.taskmanagement.entity.User;

@Service
public interface MeetingPlanUserService {
	List<User> getAllMeetingPlanUsers(String userId, UUID planId);
    
    void addMeetingPlanUser(String userId, AddMeetingPlanUserForm addForm);
    
    void removeMeetingPlanUser(String userId, UUID planId, String memberId);
    
    List<MeetingPlanUserDTO> getMemberAssignments(String userId, UUID planId);
    
    MeetingPlanUserDTO getMyAssignment(String userId, UUID planId);
    
    void updateMemberAssignment(String userId, UUID planId, UpdateMemberAssignmentsForm assignments);
    
    MeetingAutoAssignResponseDTO autoAssignMembers(String userId, UUID planId, MeetingAutoAssignRequestDTO request);
}
