package com.hust.openerp.taskmanagement.service.implement;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hust.openerp.taskmanagement.dto.form.EventUserForm;
import com.hust.openerp.taskmanagement.entity.EventUser;
import com.hust.openerp.taskmanagement.entity.User;
import com.hust.openerp.taskmanagement.exception.ApiException;
import com.hust.openerp.taskmanagement.exception.ErrorCode;
import com.hust.openerp.taskmanagement.repository.EventRepository;
import com.hust.openerp.taskmanagement.repository.EventUserRepository;
import com.hust.openerp.taskmanagement.service.EventUserService;
import com.hust.openerp.taskmanagement.service.ProjectMemberService;
import com.hust.openerp.taskmanagement.service.UserService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class EventUserServiceImplement implements EventUserService {

	private final EventUserRepository eventUserRepository;

	private final EventRepository eventRepository;

	private final ProjectMemberService projectMemberService;

	private final UserService userService;

	@Override
	public List<User> getEventUsers(String userId, UUID eventId) {
		var event = eventRepository.findById(eventId).orElseThrow(() 
				-> new ApiException(ErrorCode.EVENT_NOT_EXIST));
		if (!projectMemberService.checkAddedMemberInProject(userId, event.getProjectId())) {
			throw new ApiException(ErrorCode.NOT_A_MEMBER_OF_PROJECT);
		}

		List<User> users = eventUserRepository.findUsersByEventId(eventId);
		return users;
	}

	@Override
	public void addUserToEvent(String userId, EventUserForm eventUsers) {
		var event = eventRepository.findById(eventUsers.getEventId())
				.orElseThrow(() -> new ApiException(ErrorCode.EVENT_NOT_EXIST));
		
		if (!projectMemberService.checkAddedMemberInProject(userId, event.getProjectId())) {
			throw new ApiException(ErrorCode.NOT_A_MEMBER_OF_PROJECT);
		}
		for (String memberId : eventUsers.getUserIds()) {

			User member = userService.findById(memberId);
			if (member == null) {
				throw new ApiException(ErrorCode.USER_NOT_EXIST);
			}
			EventUser res = EventUser.builder().eventId(eventUsers.getEventId()).userId(memberId).build();
			eventUserRepository.save(res);
		}
	}
}
