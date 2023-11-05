package com.hust.openerp.taskmanagement.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hust.openerp.taskmanagement.entity.TaskCategory;

@Service
public interface TaskCategoryService {

    TaskCategory getTaskCategory(String taskCategoryId);

    List<TaskCategory> getAllTaskCategory();

    TaskCategory create(TaskCategory taskCategory);

    void delete(String categoryId);
}
