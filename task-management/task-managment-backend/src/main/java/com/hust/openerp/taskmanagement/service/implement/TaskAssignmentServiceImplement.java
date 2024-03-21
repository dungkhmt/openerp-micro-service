package com.hust.openerp.taskmanagement.service.implement;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.hust.openerp.taskmanagement.dto.dao.AssignedTaskPagination;
import com.hust.openerp.taskmanagement.dto.dao.TaskDao;
import com.hust.openerp.taskmanagement.entity.TaskAssignment;
import com.hust.openerp.taskmanagement.entity.User;
import com.hust.openerp.taskmanagement.repository.TaskAssignmentRepository;
import com.hust.openerp.taskmanagement.service.TaskAssignmentService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class TaskAssignmentServiceImplement implements TaskAssignmentService {

    private final TaskAssignmentRepository taskAssignmentRepository;

    @Override
    public TaskAssignment create(TaskAssignment taskAssignment) {
        return taskAssignmentRepository.save(taskAssignment);
    }

    @Override
    public AssignedTaskPagination getAssignedTaskPaginated(String userId, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<TaskAssignment> taskAssignmentList = taskAssignmentRepository.getByAssigneeId(userId, pageable);
        List<TaskAssignment> taskAssignments = taskAssignmentList.toList();
        List<TaskDao> taskDaoList = new ArrayList<>();
        for (TaskAssignment taskAssignment : taskAssignments) {
            User user = taskAssignment.getAssignee();
            taskDaoList.add(
                    new TaskDao(
                            taskAssignment.getTask(),
                            user.getId() + " (" + user.getFirstName() + " " + user.getLastName() + ")"));
        }

        AssignedTaskPagination assignedTaskPagination = new AssignedTaskPagination();
        assignedTaskPagination.setData(taskDaoList);
        assignedTaskPagination.setTotalPage(taskAssignmentList.getTotalPages());
        return assignedTaskPagination;
    }

    public List<User> getAssignees(UUID taskId) {
        return taskAssignmentRepository.findByTaskId(taskId).stream().map(TaskAssignment::getAssignee).toList();
    }

    @Override
    public boolean checkAssignedTask(UUID taskId, String assigneeId) {
        return taskAssignmentRepository.existsByTaskIdAndAssigneeId(taskId, assigneeId);
    }
}
