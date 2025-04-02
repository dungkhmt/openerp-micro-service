package com.hust.openerp.taskmanagement.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hust.openerp.taskmanagement.entity.Status;

@Service
public interface StatusService {
    List<Status> getAllStatus();
    
    Status create(Status status);

    void delete(String statusId);
}
