package com.hust.openerp.taskmanagement.service.implement;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hust.openerp.taskmanagement.dto.EventDTO;
import com.hust.openerp.taskmanagement.dto.EventOverviewDTO;
import com.hust.openerp.taskmanagement.dto.TaskDTO;
import com.hust.openerp.taskmanagement.dto.form.CreateEventForm;
import com.hust.openerp.taskmanagement.dto.form.UpdateEventForm;
import com.hust.openerp.taskmanagement.entity.Event;
import com.hust.openerp.taskmanagement.entity.EventUser;
import com.hust.openerp.taskmanagement.entity.Task;
import com.hust.openerp.taskmanagement.entity.User;
import com.hust.openerp.taskmanagement.exception.ApiException;
import com.hust.openerp.taskmanagement.exception.ErrorCode;
import com.hust.openerp.taskmanagement.repository.EventRepository;
import com.hust.openerp.taskmanagement.repository.EventUserRepository;
import com.hust.openerp.taskmanagement.repository.ProjectRepository;
import com.hust.openerp.taskmanagement.repository.TaskRepository;
import com.hust.openerp.taskmanagement.service.EventService;
import com.hust.openerp.taskmanagement.service.ProjectMemberService;
import com.hust.openerp.taskmanagement.service.TaskService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class EventServiceImplement implements EventService {

	private final ModelMapper modelMapper;

	private final EventRepository eventRepository;

	private final EventUserRepository eventUserRepository;

	private final TaskRepository taskRepository;

	private final ProjectMemberService projectMemberService;

	private final TaskService taskService;

	private final ProjectRepository projectRepository;

	@Override
	public List<EventOverviewDTO> getAllEvents(String userId, UUID projectId) {
		if (!projectMemberService.checkAddedMemberInProject(userId, projectId)) {
			throw new ApiException(ErrorCode.NOT_A_MEMBER_OF_PROJECT);
		}
		List<Event> events = eventRepository.findByProjectIdOrderByDueDateDesc(projectId);
		return events.stream().map(entity -> {
			var dto = modelMapper.map(entity, EventOverviewDTO.class);
			List<User> eventUsers = eventUserRepository.findUsersByEventId(entity.getId());
			dto.setEventUsers(eventUsers);
			int totalTasks = 0;
			int finishedTasks = 0;
			List<TaskDTO> eventTasks = taskService.getEventTasks(userId, entity.getId());
			for (TaskDTO task : eventTasks) {
				if ("TASK_CLOSED".equals(task.getStatusId()) || "TASK_RESOLVED".equals(task.getStatusId())) {
					finishedTasks++;
				}
				totalTasks++;
			}
			dto.setTotalTasks(totalTasks);
			dto.setFinishedTasks(finishedTasks);			
			return dto;
		}).collect(Collectors.toList());
	}

	@Override
	public Event create(String userId, CreateEventForm createEventForm) {
		UUID projectId = createEventForm.getProjectId();
		String name = createEventForm.getName();
		String description = createEventForm.getDescription();
		Date dueDate = createEventForm.getDueDate();

		projectRepository.findById(projectId).orElseThrow(() -> new ApiException(ErrorCode.PROJECT_NOT_EXIST));

		if (!projectMemberService.checkAddedMemberInProject(userId, projectId)) {
			throw new ApiException(ErrorCode.NOT_A_MEMBER_OF_PROJECT);
		}

		Event event = new Event();
		var createdTime = new Date();
		UUID eventId = UUID.randomUUID();

		event.setId(eventId);
		event.setName(name);
		event.setDescription(description);
		event.setProjectId(projectId);
		event.setCreatedStamp(createdTime);
		event.setDueDate(dueDate);
		var res = eventRepository.save(event);
		
		for (String id : createEventForm.getUserIds())
			eventUserRepository.save(EventUser.builder().eventId(eventId).userId(id).build());

		return res;
	}

	@Override
	public void update(String userId, UUID eventId, UpdateEventForm updateEventForm) {
		var event = eventRepository.findById(eventId).orElseThrow(() -> new ApiException(ErrorCode.EVENT_NOT_EXIST));

		if (updateEventForm.getName() != null && !updateEventForm.getName().isEmpty()) {
			event.setName(updateEventForm.getName());
		}
		if (updateEventForm.getDueDate() != null) {
			event.setDueDate(updateEventForm.getDueDate());
		}
		if (updateEventForm.getDescription() != null ) {
			event.setDescription(updateEventForm.getDescription());
		}

		eventRepository.save(event);
		eventUserRepository.deleteByEventId(eventId);

		for (String id : updateEventForm.getUserIds())
			eventUserRepository.save(EventUser.builder().eventId(eventId).userId(id).build());

	}

	@Override
	public EventDTO getEvent(String userId, UUID eventId) {
		var event = eventRepository.findById(eventId).orElseThrow(() -> new ApiException(ErrorCode.EVENT_NOT_EXIST));

		if (!projectMemberService.checkAddedMemberInProject(userId, event.getProjectId())) {
			throw new ApiException(ErrorCode.NOT_A_MEMBER_OF_PROJECT);
		}

		return modelMapper.map(event, EventDTO.class);
	}

	@Override
	public void deleteEvent(String userId, UUID eventId) {
		// Delete Event Users
		eventUserRepository.deleteByEventId(eventId);

		// Update eventId of related Tasks to null
		List<Task> tasks = taskRepository.findByEventId(eventId);
		for (Task task : tasks) {
			task.setEventId(null);
			taskRepository.save(task);
		}

		// Delete Event
		eventRepository.deleteById(eventId);
	}

}
