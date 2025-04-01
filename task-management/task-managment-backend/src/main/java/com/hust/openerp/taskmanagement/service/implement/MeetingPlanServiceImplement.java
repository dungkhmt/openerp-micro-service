package com.hust.openerp.taskmanagement.service.implement;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hust.openerp.taskmanagement.dto.MeetingPlanDTO;
import com.hust.openerp.taskmanagement.dto.MeetingPlanUserDTO;
import com.hust.openerp.taskmanagement.dto.MeetingSessionDTO;
import com.hust.openerp.taskmanagement.dto.form.CreateMeetingPlanForm;
import com.hust.openerp.taskmanagement.dto.form.UpdateMeetingPlanForm;
import com.hust.openerp.taskmanagement.entity.MeetingPlan;
import com.hust.openerp.taskmanagement.entity.MeetingPlan_;
import com.hust.openerp.taskmanagement.entity.MeetingSession;
import com.hust.openerp.taskmanagement.entity.User;
import com.hust.openerp.taskmanagement.exception.ApiException;
import com.hust.openerp.taskmanagement.exception.ErrorCode;
import com.hust.openerp.taskmanagement.repository.MeetingPlanRepository;
import com.hust.openerp.taskmanagement.repository.MeetingSessionRepository;
import com.hust.openerp.taskmanagement.repository.StatusRepository;
import com.hust.openerp.taskmanagement.service.MeetingPlanService;
import com.hust.openerp.taskmanagement.service.MeetingPlanUserService;
import com.hust.openerp.taskmanagement.service.NotificationService;
import com.hust.openerp.taskmanagement.service.PermissionService;
import com.hust.openerp.taskmanagement.specification.MeetingPlanSpecification;
import com.hust.openerp.taskmanagement.specification.builder.GenericSpecificationsBuilder;
import com.hust.openerp.taskmanagement.util.CriteriaParser;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class MeetingPlanServiceImplement implements MeetingPlanService {

	private final MeetingPlanRepository meetingPlanRepository;
	private final MeetingSessionRepository meetingSessionRepository;
	private final StatusRepository statusRepository;
	private final PermissionService permissionService;
	private final MeetingPlanUserService meetingPlanUserService;
	private final NotificationService notificationService;
	private final ModelMapper modelMapper;

	@PersistenceContext
	private EntityManager entityManager;

	@Value("${spring.mail.username}")
	private String systemEmail;

	@Value("${app.endpoint.client}")
	private String clientEndpoint;

	@Autowired
	public MeetingPlanServiceImplement(MeetingPlanRepository meetingPlanRepository,
			MeetingSessionRepository meetingSessionRepository, StatusRepository statusRepository,
			PermissionService permissionService, MeetingPlanUserService meetingPlanUserService,
			NotificationService notificationService, ModelMapper modelMapper, EntityManager entityManager) {
		super();
		this.meetingPlanRepository = meetingPlanRepository;
		this.meetingSessionRepository = meetingSessionRepository;
		this.statusRepository = statusRepository;
		this.permissionService = permissionService;
		this.meetingPlanUserService = meetingPlanUserService;
		this.notificationService = notificationService;
		this.modelMapper = modelMapper;
		this.entityManager = entityManager;
	}

	@Override
	public Page<MeetingPlanDTO> getMeetingPlansByCreatorId(String userId, Pageable pageable, String search) {
		// default sort by created date
		if (pageable.getSort().isUnsorted()) {
			pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
					Sort.by(MeetingPlan_.CREATED_STAMP).ascending());
		}
		
		if (search != null && !search.equals("")) {
			search = "( " + search + " ) AND createdBy:" + userId;
		} else {
			search = "createdBy:" + userId;
		}

		GenericSpecificationsBuilder<MeetingPlan> builder = new GenericSpecificationsBuilder<>();
		var specs = builder.build(new CriteriaParser().parse(search), MeetingPlanSpecification::new);
		return meetingPlanRepository.findAll(specs, pageable).map(this::convertToDto);
	}

	@Override
	public Page<MeetingPlanDTO> getMeetingPlansByMemberId(String userId, Pageable pageable, String search) {
		// default sort by created date
		if (pageable.getSort().isUnsorted()) {
			pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
					Sort.by("createdStamp").descending());
		}

		if (search != null && !search.equals("")) {
			search = "( " + search + " ) AND memberId:" + userId;
		} else {
			search = "memberId:" + userId;
		}
		search += " AND statusId!PLAN_DRAFT";

		GenericSpecificationsBuilder<MeetingPlan> builder = new GenericSpecificationsBuilder<>();
		var specs = builder.build(new CriteriaParser().parse(search), MeetingPlanSpecification::new);
		Page<MeetingPlan> meetingPlans = meetingPlanRepository.findAll(specs, pageable);

		return meetingPlans.map(meetingPlan -> {
			MeetingPlanDTO dto = convertToDto(meetingPlan);

			MeetingPlanUserDTO mpu = meetingPlanUserService.getMyAssignment(userId, meetingPlan.getId());
			if (mpu != null && mpu.getMeetingSession() != null) {
				MeetingSessionDTO sessionDto = mpu.getMeetingSession();
				dto.setAssignedSession(sessionDto);
			}

			List<User> members = meetingPlanUserService.getAllMeetingPlanUsers(userId, meetingPlan.getId());
			dto.setMembers(members);
			return dto;
		});
	}

	@Override
	public MeetingPlan getMeetingPlanById(String userId, UUID id) {
		MeetingPlan mp = permissionService.checkMeetingPlanCreatorOrMember(userId, id);
		return mp;
	}

	@Override
	@Transactional
	public MeetingPlanDTO createMeetingPlan(String userId, CreateMeetingPlanForm createForm) {
		MeetingPlan meetingPlan = modelMapper.map(createForm, MeetingPlan.class);
		UUID id = UUID.randomUUID();
		meetingPlan.setId(id);
		meetingPlan.setCreatedBy(userId);
		meetingPlan.setCreatedStamp(new Date());
		meetingPlanRepository.save(meetingPlan);
		return modelMapper.map(meetingPlan, MeetingPlanDTO.class);
	}

	@Override
	@Transactional
	public void updateMeetingPlan(String userId, UUID planId, UpdateMeetingPlanForm updateForm) {
		MeetingPlan mp = permissionService.checkMeetingPlanCreator(userId, planId);
		modelMapper.map(updateForm, mp);
		meetingPlanRepository.save(mp);

		List<User> members = meetingPlanUserService.getAllMeetingPlanUsers(userId, planId);
		for (User member : members) {
			try {
				notificationService.createInAppNotification(userId, member.getId(),
						"Cuộc họp \"" + mp.getName() + "\" vừa được cập nhật.", "/meetings/joined-meetings/" + planId);
			} catch (Exception e) {
				e.printStackTrace();
				// TODO: handle exception
			}
		}

	}

	@Override
	@Transactional
	public void deleteMeetingPlan(String userId, UUID planId) {
		MeetingPlan mp = permissionService.checkMeetingPlanCreator(userId, planId);
		meetingPlanRepository.delete(mp);
	}

	@Override
	@Transactional
	public void updateStatus(String userId, UUID planId, String statusId) {
		MeetingPlan plan = permissionService.checkMeetingPlanCreator(userId, planId);

		String currentStatusId = plan.getStatus().getStatusId();
		if (!statusRepository.findByStatusId(statusId).isPresent()) {
			throw new ApiException(ErrorCode.STATUS_NOT_FOUND);
		}

		Map<String, Set<String>> transitions = Map.of("PLAN_DRAFT", Set.of("PLAN_REG_OPEN", "PLAN_CANCELED"),
				"PLAN_REG_OPEN", Set.of("PLAN_CANCELED"), "PLAN_REG_CLOSED", Set.of("PLAN_ASSIGNED", "PLAN_CANCELED"),
				"PLAN_ASSIGNED", Set.of("PLAN_CANCELED"), "PLAN_IN_PROGRESS", Set.of("PLAN_CANCELED"));

		if (!transitions.containsKey(currentStatusId) || !transitions.get(currentStatusId).contains(statusId)) {
			throw new ApiException(ErrorCode.INVALID_STATUS_TRANSITION,
					"Invalid status transition from " + currentStatusId + " to " + statusId);
		}

		meetingPlanRepository.updateStatus(planId, statusId);

		// Send in-app notifications and email notifications
		String subject = "";
		String mailForm = "";
		Map<String, Object> model = new HashMap<>();
		String link = clientEndpoint + "/meetings/joined-meetings/" + planId;
		User creator = plan.getCreator();
		String creatorName = creator.getFirstName() == null ? ""
				: creator.getFirstName() + " " + creator.getLastName() == null ? "" : creator.getLastName();
		model.put("meetingCreator", creatorName.isBlank() ? plan.getCreatedBy() : creatorName);
		model.put("meetingName", plan.getName());
		model.put("location", plan.getLocation());
		model.put("meetingDescription", plan.getDescription());
		model.put("meetingLink", link);

		List<User> members = meetingPlanUserService.getAllMeetingPlanUsers(userId, planId);
		for (User member : members) {
			if (statusId.equals("PLAN_REG_OPEN")) {
				subject = "Bạn được thêm vào cuộc họp \"" + plan.getName() + "\".";
				mailForm = "new-meeting";
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy, HH:mm");
				String formattedDate = formatter.format(plan.getRegistrationDeadline());
				model.put("registrationDeadline", formattedDate);
			} else if (statusId.equals("PLAN_ASSIGNED")) {
				MeetingPlanUserDTO mpu = meetingPlanUserService.getMyAssignment(member.getId(), planId);
				if (mpu != null && mpu.getMeetingSession() != null) {
					MeetingSessionDTO sessionDto = mpu.getMeetingSession();
					SimpleDateFormat startTimeFormatter = new SimpleDateFormat("dd/MM/yyyy, 'từ' HH:mm");
					SimpleDateFormat endTimeFormatter = new SimpleDateFormat("HH:mm");
					String formattedDate = startTimeFormatter.format(sessionDto.getStartTime()) + " - "
							+ endTimeFormatter.format(sessionDto.getEndTime());
					subject = "Bạn đã được phân công vào phiên họp vào ngày " + formattedDate + " trong cuộc họp \""
							+ plan.getName() + "\".";
					mailForm = "meeting-session-assign";
					model.put("assignedSession", formattedDate);
				}
			} else if (statusId.equals("PLAN_CANCELED")) {
				subject = "Cuộc họp \"" + plan.getName() + "\" đã bị hủy.";
				mailForm = "cancel-meeting";
			} else {
				break;
			}

			try {
				notificationService.createInAppNotification(userId, member.getId(), subject, link);

				String emailMember = member.getEmail();
				if (emailMember != null) {
					String memberName = member.getFirstName() == null ? ""
							: member.getFirstName() + " " + member.getLastName() == null ? "" : member.getLastName();
					model.put("userName", memberName.isBlank() ? member.getId() : memberName);
					notificationService.createMailNotification(creator.getEmail(), emailMember, subject, mailForm,
							model);
				}
			} catch (Exception e) {
				e.printStackTrace();
				// TODO: handle exception
			}
		}

	}

	@Override
	@Transactional
	public void closeRegistrations(Date now) {
		entityManager.joinTransaction();

		List<MeetingPlan> plansToClose = meetingPlanRepository
				.findByStatusIdAndRegistrationDeadlineBefore("PLAN_REG_OPEN", now);
		if (!plansToClose.isEmpty()) {
			for (MeetingPlan plan : plansToClose) {
				plan.setStatusId("PLAN_REG_CLOSED");

				try {
					String link = clientEndpoint + "/meetings/created-meetings/" + plan.getId();
					String message = "Cuộc họp \"" + plan.getName()
							+ "\" đã đóng đăng ký. Vui lòng phân công thành viên!";
					notificationService.createInAppNotification("system", plan.getCreatedBy(), message, link);

					Map<String, Object> model = new HashMap<>();
					User creator = plan.getCreator();
					String creatorName = creator.getFirstName() == null ? ""
							: creator.getFirstName() + " " + creator.getLastName() == null ? "" : creator.getLastName();
					model.put("meetingCreator", creatorName.isBlank() ? plan.getCreatedBy() : creatorName);
					model.put("meetingName", plan.getName());
					model.put("location", plan.getLocation());
					model.put("meetingDescription", plan.getDescription());
					model.put("meetingLink", link);

					notificationService.createMailNotification(systemEmail, creator.getEmail(), message,
							"meeting-reg-closed", model);
				} catch (Exception e) {
					e.printStackTrace();
					// TODO: handle exception
				}
			}
			meetingPlanRepository.saveAll(plansToClose);
		}
	}

	@Override
	@Transactional
	public void assignMeetingPlans(Date now) {
		entityManager.joinTransaction();

		List<MeetingPlan> plansToStart = meetingPlanRepository.findByStatusId("PLAN_ASSIGNED");
		for (MeetingPlan plan : plansToStart) {
			Optional<MeetingSession> earliestSession = meetingSessionRepository
					.findFirstByPlanIdOrderByStartTimeAsc(plan.getId());
			if (earliestSession.isPresent() && earliestSession.get().getStartTime().before(now)) {
				plan.setStatusId("PLAN_IN_PROGRESS");
				meetingPlanRepository.save(plan);
			}
		}
	}

	@Override
	@Transactional
	public void completeMeetingPlans(Date now) {
		entityManager.joinTransaction();

		List<MeetingPlan> plansInProgress = meetingPlanRepository.findByStatusId("PLAN_IN_PROGRESS");
		for (MeetingPlan plan : plansInProgress) {
			Optional<MeetingSession> latestSession = meetingSessionRepository
					.findFirstByPlanIdOrderByEndTimeDesc(plan.getId());
			if (latestSession.isPresent() && latestSession.get().getEndTime().before(now)) {
				plan.setStatusId("PLAN_COMPLETED");
				meetingPlanRepository.save(plan);
			}
		}
	}

	private MeetingPlanDTO convertToDto(MeetingPlan meetingPlan) {
		return modelMapper.map(meetingPlan, MeetingPlanDTO.class);
	}
}
