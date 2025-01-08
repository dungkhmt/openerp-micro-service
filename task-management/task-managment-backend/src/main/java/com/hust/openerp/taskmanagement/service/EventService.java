package com.hust.openerp.taskmanagement.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.hust.openerp.taskmanagement.dto.EventDTO;
import com.hust.openerp.taskmanagement.dto.EventOverviewDTO;
import com.hust.openerp.taskmanagement.dto.form.CreateEventForm;
import com.hust.openerp.taskmanagement.dto.form.UpdateEventForm;
import com.hust.openerp.taskmanagement.entity.Event;

@Service
public interface EventService {
	List<EventOverviewDTO> getAllEvents(String userId, UUID projectId);

	Event create(String userId, CreateEventForm createEventForm);
	
	void update(String userId, UUID eventId, UpdateEventForm updateEventForm);

	EventDTO getEvent(String userId, UUID eventId);
	
	void deleteEvent(String userId, UUID eventId);
}
