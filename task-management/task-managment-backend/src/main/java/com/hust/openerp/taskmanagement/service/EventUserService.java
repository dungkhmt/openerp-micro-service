package com.hust.openerp.taskmanagement.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.hust.openerp.taskmanagement.dto.form.EventUserForm;
import com.hust.openerp.taskmanagement.entity.User;

@Service
public interface EventUserService {
	List<User> getEventUsers(String userId, UUID eventId);

    void addUserToEvent(String userId, EventUserForm eventUsers);
}
