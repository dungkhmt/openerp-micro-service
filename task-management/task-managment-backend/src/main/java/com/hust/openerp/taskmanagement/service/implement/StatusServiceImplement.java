package com.hust.openerp.taskmanagement.service.implement;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hust.openerp.taskmanagement.entity.Status;
import com.hust.openerp.taskmanagement.exception.ApiException;
import com.hust.openerp.taskmanagement.exception.ErrorCode;
import com.hust.openerp.taskmanagement.repository.TaskRepository;
import com.hust.openerp.taskmanagement.repository.StatusRepository;
import com.hust.openerp.taskmanagement.service.StatusService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class StatusServiceImplement implements StatusService {

    private final StatusRepository statusRepository;
    
    private final TaskRepository taskRepository;

    @Override
    public List<Status> getAllStatus() {
        return statusRepository.findAllByOrderByCreatedStampDesc();
    }

	@Override
	public Status create(Status status) {
		if (statusRepository.existsById(status.getStatusId())) {
    		throw new ApiException(ErrorCode.STATUS_EXIST);
        }
		
		String code = status.getStatusCode();
		if (statusRepository.findByStatusCode(code).isPresent()) {
	        throw new ApiException(ErrorCode.STATUS_EXIST);
	    }
		
		String description = status.getDescription();
		if (statusRepository.findByDescription(description).isPresent()) {
	        throw new ApiException(ErrorCode.STATUS_EXIST);
	    }
    	
        return statusRepository.save(status);

	}

	@Override
	@Transactional
	public void delete(String statusId) {
		taskRepository.updateStatusToDefault(statusId);
    	
		statusRepository.deleteById(statusId);
	}
}
