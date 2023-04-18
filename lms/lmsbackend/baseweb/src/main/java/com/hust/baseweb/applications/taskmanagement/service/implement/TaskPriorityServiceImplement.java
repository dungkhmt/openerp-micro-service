package com.hust.baseweb.applications.taskmanagement.service.implement;

import com.hust.baseweb.applications.taskmanagement.entity.TaskPriority;
import com.hust.baseweb.applications.taskmanagement.repository.TaskPriorityRepository;
import com.hust.baseweb.applications.taskmanagement.service.TaskPriorityService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class TaskPriorityServiceImplement implements TaskPriorityService {

    private final TaskPriorityRepository taskPriorityRepository;

    @Override
    public TaskPriority getTaskPriorityById(String taskPriorityId) {
        return taskPriorityRepository.getOne(taskPriorityId);
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
