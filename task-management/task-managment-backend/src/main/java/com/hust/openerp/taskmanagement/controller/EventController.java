package com.hust.openerp.taskmanagement.controller;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hust.openerp.taskmanagement.config.OpenApiConfig;
import com.hust.openerp.taskmanagement.dto.EventDTO;
import com.hust.openerp.taskmanagement.dto.EventOverviewDTO;
import com.hust.openerp.taskmanagement.dto.form.CreateEventForm;
import com.hust.openerp.taskmanagement.dto.form.UpdateEventForm;
import com.hust.openerp.taskmanagement.entity.Event;
import com.hust.openerp.taskmanagement.service.EventService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/events")
@Tag(name = "Event", description = "APIs for event management")
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
@RequiredArgsConstructor
public class EventController {
	private final EventService eventService;

	@GetMapping
	public List<EventOverviewDTO> getAllEvents(Principal principal, 
			@RequestParam("projectId") UUID projectId) {
		return eventService.getAllEvents(principal.getName(), projectId);
	}

	@GetMapping("{eventId}")
	public EventDTO getEvent(Principal principal, 
			@PathVariable("eventId") UUID eventId) {
		return eventService.getEvent(principal.getName(), eventId);
	}

	@PostMapping
	public Event createEvent(Principal principal, 
			@RequestBody @Valid CreateEventForm createEventForm) {
		return eventService.create(principal.getName(), createEventForm);
	}
	
	@PutMapping("{eventId}")
	public void updateEvent(Principal principal, @PathVariable("eventId") UUID eventId,
			@RequestBody @Valid UpdateEventForm updateEventForm) {
		eventService.update(principal.getName(), eventId, updateEventForm);
	}
	
	@DeleteMapping("{eventId}")
	public void deleteEvent(Principal principal, @PathVariable("eventId") UUID eventId) {
		eventService.deleteEvent(principal.getName(), eventId);
	}

}
