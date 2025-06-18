package com.hust.openerp.taskmanagement.service;

import com.hust.openerp.taskmanagement.dto.MeetingAutoAssignResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public interface AssignmentService{
    MeetingAutoAssignResponseDTO autoAssign(Map<String, List<UUID>> preferences);
}
