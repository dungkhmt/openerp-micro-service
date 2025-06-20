package com.hust.openerp.taskmanagement.service.implement;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hust.openerp.taskmanagement.entity.EventUser;
import com.hust.openerp.taskmanagement.entity.User;
import com.hust.openerp.taskmanagement.exception.ApiException;
import com.hust.openerp.taskmanagement.exception.ErrorCode;
import com.hust.openerp.taskmanagement.repository.EventRepository;
import com.hust.openerp.taskmanagement.repository.EventUserRepository;
import com.hust.openerp.taskmanagement.service.EventUserService;
import com.hust.openerp.taskmanagement.service.NotificationService;
import com.hust.openerp.taskmanagement.service.ProjectMemberService;
import com.hust.openerp.taskmanagement.service.UserService;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EventUserServiceImplement implements EventUserService {

    private static final Logger log = LoggerFactory.getLogger(EventUserServiceImplement.class);

	private final EventUserRepository eventUserRepository;

	private final EventRepository eventRepository;

	private final ProjectMemberService projectMemberService;

	private final UserService userService;

	private final NotificationService notiService;

	@Value("${app.endpoint.client}")
	private String clientEndpoint;

	@Override
	public List<User> getEventUsers(String userId, UUID eventId) {
		var event = eventRepository.findById(eventId).orElseThrow(() -> new ApiException(ErrorCode.EVENT_NOT_FOUND));
		if (!projectMemberService.checkAddedMemberInProject(userId, event.getProjectId())) {
			throw new ApiException(ErrorCode.NOT_A_MEMBER_OF_PROJECT);
		}

        return eventUserRepository.findUsersByEventId(eventId);
	}

	@Override
	public void addUserToEvent(String adderId, String memberId, UUID eventId) {
		var event = eventRepository.findById(eventId).orElseThrow(
				() -> new ApiException(ErrorCode.EVENT_NOT_FOUND));

		if (!projectMemberService.checkAddedMemberInProject(adderId, event.getProjectId())) {
			throw new ApiException(ErrorCode.NOT_A_MEMBER_OF_PROJECT);
		}

		User member = userService.findById(memberId);
		if (member == null) {
			throw new ApiException(ErrorCode.USER_NOT_EXIST);
		}

		EventUser res = EventUser.builder().eventId(eventId).userId(memberId).build();
		eventUserRepository.save(res);

		// Send notifications if the added user is not the one performing the operation
		try {
			if (adderId != null && adderId.equals(member.getId())) {
				return;
			}

			String subject = "Bạn được thêm vào sự kiện: " + event.getName();

			notiService.createInAppNotification(adderId, member.getId(), subject,
					"/project/" + event.getProjectId() + "/events/" + event.getId());
		} catch (Exception e) {
            log.error("Failed to send notifications when adding user {} to event {}: {}", memberId, eventId, e.getMessage(), e);
		}
	}

}
