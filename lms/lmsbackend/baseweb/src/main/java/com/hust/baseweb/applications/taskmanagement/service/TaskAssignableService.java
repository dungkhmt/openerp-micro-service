package com.hust.baseweb.applications.taskmanagement.service;

import com.hust.baseweb.applications.taskmanagement.dto.dao.AssignedTaskPagination;
import com.hust.baseweb.applications.taskmanagement.entity.TaskAssignable;
import com.hust.baseweb.applications.taskmanagement.entity.TaskAssignment;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface TaskAssignableService {
    TaskAssignment create(TaskAssignment taskAssignment);

    TaskAssignment getByTaskId(UUID taskId);

    AssignedTaskPagination getAssignedTaskPaginated(UUID partyId, int pageNo, int pageSize);
}
