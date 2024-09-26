package com.hust.openerp.taskmanagement.service.implement;

import com.hust.openerp.taskmanagement.entity.TaskPriority;
import com.hust.openerp.taskmanagement.repository.TaskPriorityRepository;
import com.hust.openerp.taskmanagement.service.TaskPriorityService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class TaskPriorityServiceImplement implements TaskPriorityService {

    private final TaskPriorityRepository taskPriorityRepository;

    @Override
    public TaskPriority getTaskPriorityById(String taskPriorityId) {
        return taskPriorityRepository.findById(taskPriorityId).orElse(null);
    }

    @Override
    public List<TaskPriority> getAll() {
        return taskPriorityRepository.findAll();
    }

    @Override
    public TaskPriority create(TaskPriority taskPriority) {
        return taskPriorityRepository.save(taskPriority);
    }

    @Override
    public void delete(String priorityId) {
        taskPriorityRepository.deleteById(priorityId);
    }
}
