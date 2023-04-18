package com.hust.baseweb.applications.taskmanagement.service.implement;

import com.hust.baseweb.applications.taskmanagement.entity.TaskCategory;
import com.hust.baseweb.applications.taskmanagement.repository.TaskCategoryRepository;
import com.hust.baseweb.applications.taskmanagement.service.TaskCategoryService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class TaskCategoryServiceImplement implements TaskCategoryService {

    private final TaskCategoryRepository taskCategoryRepository;

    @Override
    public TaskCategory getTaskCategory(String taskCategoryId) {
        return taskCategoryRepository.getOne(taskCategoryId);
    }

    @Override
    public List<TaskCategory> getAllTaskCategory() {
        return taskCategoryRepository.findAll();
    }

    @Override
    public TaskCategory create(TaskCategory taskCategory) {
        return taskCategoryRepository.save(taskCategory);
    }

    @Override
    public void delete(String categoryId) {
        taskCategoryRepository.deleteById(categoryId);
    }
}
