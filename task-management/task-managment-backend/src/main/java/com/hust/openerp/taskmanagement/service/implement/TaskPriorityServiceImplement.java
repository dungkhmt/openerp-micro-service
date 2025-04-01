package com.hust.openerp.taskmanagement.service.implement;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hust.openerp.taskmanagement.entity.TaskPriority;
import com.hust.openerp.taskmanagement.exception.ApiException;
import com.hust.openerp.taskmanagement.exception.ErrorCode;
import com.hust.openerp.taskmanagement.repository.TaskPriorityRepository;
import com.hust.openerp.taskmanagement.repository.TaskRepository;
import com.hust.openerp.taskmanagement.service.TaskPriorityService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class TaskPriorityServiceImplement implements TaskPriorityService {

    private final TaskPriorityRepository taskPriorityRepository;
    
    private final TaskRepository taskRepository;

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
    	if (taskPriorityRepository.existsById(taskPriority.getPriorityId())) {
    		throw new ApiException(ErrorCode.PRIORITY_EXIST);
        }
    	
    	String name = taskPriority.getPriorityName();
    	taskPriorityRepository.findByPriorityName(name).ifPresent(existingPriority -> {
        	throw new ApiException(ErrorCode.PRIORITY_EXIST);
        });
        return taskPriorityRepository.save(taskPriority);
    }

    @Override
    @Transactional
    public void delete(String priorityId) {
    	taskRepository.updatePriorityToDefault(priorityId);
    	
        taskPriorityRepository.deleteById(priorityId);
    }
}
