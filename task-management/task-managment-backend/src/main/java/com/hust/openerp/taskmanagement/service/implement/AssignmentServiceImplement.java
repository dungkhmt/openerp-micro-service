package com.hust.openerp.taskmanagement.service.implement;

import com.hust.openerp.taskmanagement.algorithm.AssignmentStrategy;
import com.hust.openerp.taskmanagement.algorithm.HopcroftKarpBinarySearch;
import com.hust.openerp.taskmanagement.dto.MeetingAutoAssignResponseDTO;
import com.hust.openerp.taskmanagement.service.AssignmentService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class AssignmentServiceImplement implements AssignmentService {
    @Override
    public MeetingAutoAssignResponseDTO autoAssign(Map<String, List<UUID>> preferences) {
        AssignmentStrategy strategy = new HopcroftKarpBinarySearch(preferences);
        int minExtraCapacity = strategy.findMinExtraCapacity();
        Map<String, UUID> assignment = strategy.getAssignment();
        return new MeetingAutoAssignResponseDTO(assignment, minExtraCapacity);
    }
}
