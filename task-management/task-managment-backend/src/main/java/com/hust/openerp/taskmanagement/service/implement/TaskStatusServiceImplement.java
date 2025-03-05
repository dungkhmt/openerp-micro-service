package com.hust.openerp.taskmanagement.service.implement;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hust.openerp.taskmanagement.entity.TaskStatus;
import com.hust.openerp.taskmanagement.exception.ApiException;
import com.hust.openerp.taskmanagement.exception.ErrorCode;
import com.hust.openerp.taskmanagement.repository.TaskRepository;
import com.hust.openerp.taskmanagement.repository.TaskStatusRepository;
import com.hust.openerp.taskmanagement.service.TaskStatusService;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class TaskStatusServiceImplement implements TaskStatusService {

    private final TaskStatusRepository taskStatusRepository;
    
    private final TaskRepository taskRepository;

    @Override
    public List<TaskStatus> getAllTaskStatus() {
        return taskStatusRepository.findAllByOrderByCreatedStampDesc();
    }

	@Override
	public TaskStatus create(TaskStatus taskStatus) {
		if (taskStatusRepository.existsById(taskStatus.getStatusId())) {
    		throw new ApiException(ErrorCode.STATUS_EXIST);
        }
		
		String code = taskStatus.getStatusCode();
		if (taskStatusRepository.findByStatusCode(code).isPresent()) {
	        throw new ApiException(ErrorCode.STATUS_EXIST);
	    }
		
		String description = taskStatus.getDescription();
		if (taskStatusRepository.findByDescription(description).isPresent()) {
	        throw new ApiException(ErrorCode.STATUS_EXIST);
	    }
    	
        return taskStatusRepository.save(taskStatus);

	}

	@Override
	@Transactional
	public void delete(String statusId) {
		taskRepository.updateStatusToDefault(statusId);
    	
		taskStatusRepository.deleteById(statusId);
	}
}
