package com.hust.openerp.taskmanagement.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.hust.openerp.taskmanagement.dto.dao.AssignedTaskPagination;
import com.hust.openerp.taskmanagement.entity.TaskAssignment;

@Service
public interface TaskAssignableService {
    TaskAssignment create(TaskAssignment taskAssignment);

    TaskAssignment getByTaskId(UUID taskId);

    AssignedTaskPagination getAssignedTaskPaginated(String userId, int pageNo, int pageSize);
}
