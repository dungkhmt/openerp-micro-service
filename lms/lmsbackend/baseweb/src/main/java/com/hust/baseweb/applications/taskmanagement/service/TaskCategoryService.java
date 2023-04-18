package com.hust.baseweb.applications.taskmanagement.service;

import com.hust.baseweb.applications.taskmanagement.entity.TaskCategory;
import com.hust.baseweb.applications.taskmanagement.entity.TaskPriority;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface TaskCategoryService {

    TaskCategory getTaskCategory(String taskCategoryId);

    List<TaskCategory> getAllTaskCategory();

    TaskCategory create(TaskCategory taskCategory);

    void delete(String categoryId);
}
