package com.hust.openerp.taskmanagement.service.implement;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
public class EventUserServiceImplement implements EventUserService {

	private final EventUserRepository eventUserRepository;

	private final EventRepository eventRepository;

	private final ProjectMemberService projectMemberService;

	private final UserService userService;

	private NotificationService notiService;

	@Value("${app.endpoint.client}")
	private String clientEndpoint;

	@Autowired
	public EventUserServiceImplement(EventUserRepository eventUserRepository, EventRepository eventRepository,
			ProjectMemberService projectMemberService, UserService userService, NotificationService notiService) {
		super();
		this.eventUserRepository = eventUserRepository;
		this.eventRepository = eventRepository;
		this.projectMemberService = projectMemberService;
		this.userService = userService;
		this.notiService = notiService;
	}

	@Override
	public List<User> getEventUsers(String userId, UUID eventId) {
		var event = eventRepository.findById(eventId).orElseThrow(() -> new ApiException(ErrorCode.EVENT_NOT_FOUND));
		if (!projectMemberService.checkAddedMemberInProject(userId, event.getProjectId())) {
			throw new ApiException(ErrorCode.NOT_A_MEMBER_OF_PROJECT);
		}

		List<User> users = eventUserRepository.findUsersByEventId(eventId);
		return users;
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
			if (member == null || (adderId != null && adderId.equals(member.getId()))) {
				return;
			}

			String subject = "Bạn được thêm vào sự kiện: " + event.getName();

			notiService.createInAppNotification(adderId, member.getId(), subject,
					"/project/" + event.getProjectId() + "/events/" + event.getId());

			if (member.getEmail() == null || member.getEmail().isEmpty()) {
				return;
			}

			User adder = userService.findById(adderId);
			Map<String, Object> model = new HashMap<>();
			model.put("participantName",
					(member.getFirstName() == null && member.getLastName() == null) ? member.getId()
							: member.getFirstName() + " " + member.getLastName());
			model.put("eventName", event.getName());
			model.put("eventDate", event.getDueDate() == null ? "Không xác định"
					: new SimpleDateFormat("dd-MM-yyyy HH:mm").format(event.getDueDate()));
			model.put("link", clientEndpoint + "/project/" + event.getProjectId() + "/event/" + event.getId());

			notiService.createMailNotification(adder.getEmail(), member.getEmail(), subject, "add-event", model);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
