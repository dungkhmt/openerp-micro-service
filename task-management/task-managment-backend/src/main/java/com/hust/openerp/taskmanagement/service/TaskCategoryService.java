package com.hust.openerp.taskmanagement.service;

import com.hust.openerp.taskmanagement.entity.TaskCategory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TaskCategoryService {

    TaskCategory getTaskCategory(String taskCategoryId);

    List<TaskCategory> getAllTaskCategory();

    TaskCategory create(TaskCategory taskCategory);

    void delete(String categoryId);
}
