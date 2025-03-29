package com.hust.openerp.taskmanagement.service.implement;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hust.openerp.taskmanagement.entity.TaskCategory;
import com.hust.openerp.taskmanagement.exception.ApiException;
import com.hust.openerp.taskmanagement.exception.ErrorCode;
import com.hust.openerp.taskmanagement.repository.TaskCategoryRepository;
import com.hust.openerp.taskmanagement.repository.TaskRepository;
import com.hust.openerp.taskmanagement.service.TaskCategoryService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class TaskCategoryServiceImplement implements TaskCategoryService {

    private final TaskCategoryRepository taskCategoryRepository;
    
    private final TaskRepository taskRepository;

    @Override
    public TaskCategory getTaskCategory(String taskCategoryId) {
        return taskCategoryRepository.findById(taskCategoryId).orElse(null);
    }

    @Override
    public List<TaskCategory> getAllTaskCategory() {
        return taskCategoryRepository.findAll();
    }

    @Override
    public TaskCategory create(TaskCategory taskCategory) {
    	if (taskCategoryRepository.existsById(taskCategory.getCategoryId())) {
    		throw new ApiException(ErrorCode.CATEGORY_EXIST);
        }
    	
		String name = taskCategory.getCategoryName();
		taskCategoryRepository.findByCategoryName(name).ifPresent(existingCategory -> {
        	throw new ApiException(ErrorCode.CATEGORY_EXIST);
        });

        return taskCategoryRepository.save(taskCategory);
    }

    @Override
    @Transactional
    public void delete(String categoryId) {
    	taskRepository.updateCategoryToDefault(categoryId);
    	
        taskCategoryRepository.deleteById(categoryId);
    }
}
