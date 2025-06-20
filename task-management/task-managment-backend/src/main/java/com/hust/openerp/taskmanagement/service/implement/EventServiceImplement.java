package com.hust.openerp.taskmanagement.service.implement;

import com.hust.openerp.taskmanagement.dto.EventDTO;
import com.hust.openerp.taskmanagement.dto.EventOverviewDTO;
import com.hust.openerp.taskmanagement.dto.TaskDTO;
import com.hust.openerp.taskmanagement.dto.form.CreateEventForm;
import com.hust.openerp.taskmanagement.dto.form.UpdateEventForm;
import com.hust.openerp.taskmanagement.entity.Event;
import com.hust.openerp.taskmanagement.entity.Task;
import com.hust.openerp.taskmanagement.entity.User;
import com.hust.openerp.taskmanagement.exception.ApiException;
import com.hust.openerp.taskmanagement.exception.ErrorCode;
import com.hust.openerp.taskmanagement.model.TaskStatusEnum;
import com.hust.openerp.taskmanagement.repository.EventRepository;
import com.hust.openerp.taskmanagement.repository.EventUserRepository;
import com.hust.openerp.taskmanagement.repository.ProjectRepository;
import com.hust.openerp.taskmanagement.repository.TaskRepository;
import com.hust.openerp.taskmanagement.service.EventService;
import com.hust.openerp.taskmanagement.service.EventUserService;
import com.hust.openerp.taskmanagement.service.ProjectMemberService;
import com.hust.openerp.taskmanagement.service.TaskService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class EventServiceImplement implements EventService {

    private final ModelMapper modelMapper;

    private final EventRepository eventRepository;

    private final EventUserRepository eventUserRepository;

    private final EventUserService eventUserService;

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
                if (TaskStatusEnum.TASK_RESOLVED.getStatusId().equals(task.getStatusId())) {
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
    @Transactional
    public Event create(String userId, CreateEventForm createEventForm) {
        UUID projectId = createEventForm.getProjectId();
        String name = createEventForm.getName();
        String description = createEventForm.getDescription();
        Date startDate = createEventForm.getStartDate();
        Date dueDate = createEventForm.getDueDate();

        projectRepository.findById(projectId).orElseThrow(
            () -> new ApiException(ErrorCode.PROJECT_NOT_EXIST));

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
        event.setStartDate(startDate);
        event.setDueDate(dueDate);
        var res = eventRepository.save(event);

        for (String memberId : createEventForm.getUserIds())
            eventUserService.addUserToEvent(userId, memberId, eventId);

        return res;
    }

    public void update(String userId, UUID eventId, UpdateEventForm updateEventForm) {
        var event = eventRepository.findById(eventId).orElseThrow(
            () -> new ApiException(ErrorCode.EVENT_NOT_FOUND));

        if (!projectMemberService.checkAddedMemberInProject(userId, event.getProjectId())) {
            throw new ApiException(ErrorCode.NOT_A_MEMBER_OF_PROJECT);
        }

        if (updateEventForm.getName() != null && !updateEventForm.getName().isEmpty()) {
            event.setName(updateEventForm.getName());
        }
        if (updateEventForm.getStartDate() != null) {
            event.setStartDate(updateEventForm.getStartDate());
        }
        if (updateEventForm.getDueDate() != null) {
            event.setDueDate(updateEventForm.getDueDate());
        }
        if (updateEventForm.getDescription() != null) {
            event.setDescription(updateEventForm.getDescription());
        }

        eventRepository.save(event);

        List<User> currentAssociations = eventUserRepository.findUsersByEventId(eventId);
        Set<String> currentUserIds = currentAssociations.stream()
            .map(User::getId)
            .collect(Collectors.toSet());

        Set<String> newUserIds = new HashSet<>(updateEventForm.getUserIds());

        Set<String> newlyAddedUserIds = new HashSet<>(newUserIds);
        newlyAddedUserIds.removeAll(currentUserIds);

        Set<String> removedUserIds = new HashSet<>(currentUserIds);
        removedUserIds.removeAll(newUserIds);
        for (String removedId : removedUserIds) {
            eventUserRepository.deleteByEventIdAndUserId(eventId, removedId);
        }

        // For each newly added user, add the member to the event and send notifications
        for (String newMemberId : newlyAddedUserIds)
            eventUserService.addUserToEvent(userId, newMemberId, eventId);
    }

    @Override
    public EventDTO getEvent(String userId, UUID eventId) {
        var event = eventRepository.findById(eventId).orElseThrow(() -> new ApiException(ErrorCode.EVENT_NOT_FOUND));

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
