package com.hust.baseweb.applications.taskmanagement.service;

import com.hust.baseweb.applications.taskmanagement.entity.TaskPriority;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface TaskPriorityService {

    TaskPriority getTaskPriorityById(String taskPriorityId);

    List<TaskPriority> getAll();

    TaskPriority create(TaskPriority taskPriority);

    void delete(String priorityId);
}
