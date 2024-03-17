package com.hust.openerp.taskmanagement.service;

import com.hust.openerp.taskmanagement.dto.dao.AssignedTaskPagination;
import com.hust.openerp.taskmanagement.entity.TaskAssignment;
import com.hust.openerp.taskmanagement.entity.User;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Deprecated
public interface TaskAssignmentService {
    TaskAssignment create(TaskAssignment taskAssignment);

    /**
     * @deprecated
     * @param userId
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Deprecated
    AssignedTaskPagination getAssignedTaskPaginated(String userId, int pageNo, int pageSize);

    List<User> getAssignees(UUID taskId);

    boolean checkAssignedTask(UUID taskId, String assigneeId);
}
