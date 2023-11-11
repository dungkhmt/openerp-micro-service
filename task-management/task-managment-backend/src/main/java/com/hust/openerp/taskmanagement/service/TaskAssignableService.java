package com.hust.openerp.taskmanagement.service;

import com.hust.openerp.taskmanagement.dto.dao.AssignedTaskPagination;
import com.hust.openerp.taskmanagement.entity.TaskAssignment;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface TaskAssignableService {
    TaskAssignment create(TaskAssignment taskAssignment);

    TaskAssignment getByTaskId(UUID taskId);

    AssignedTaskPagination getAssignedTaskPaginated(String userId, int pageNo, int pageSize);
}
