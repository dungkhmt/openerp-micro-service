package com.hust.openerp.taskmanagement.service.implement;

import com.hust.openerp.taskmanagement.dto.MeetingPlanDTO;
import com.hust.openerp.taskmanagement.dto.MeetingSessionDTO;
import com.hust.openerp.taskmanagement.dto.form.CreateMeetingPlanForm;
import com.hust.openerp.taskmanagement.dto.form.UpdateMeetingPlanForm;
import com.hust.openerp.taskmanagement.entity.*;
import com.hust.openerp.taskmanagement.exception.ApiException;
import com.hust.openerp.taskmanagement.exception.ErrorCode;
import com.hust.openerp.taskmanagement.model.MeetingPlanStatusEnum;
import com.hust.openerp.taskmanagement.multitenancy.util.OrganizationContext;
import com.hust.openerp.taskmanagement.repository.MeetingPlanRepository;
import com.hust.openerp.taskmanagement.repository.MeetingPlanUserRepository;
import com.hust.openerp.taskmanagement.repository.MeetingSessionRepository;
import com.hust.openerp.taskmanagement.service.MeetingPlanService;
import com.hust.openerp.taskmanagement.service.MeetingPlanUserService;
import com.hust.openerp.taskmanagement.service.NotificationService;
import com.hust.openerp.taskmanagement.service.PermissionService;
import com.hust.openerp.taskmanagement.specification.MeetingPlanSpecification;
import com.hust.openerp.taskmanagement.specification.builder.GenericSpecificationsBuilder;
import com.hust.openerp.taskmanagement.util.CriteriaParser;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.hust.openerp.taskmanagement.multitenancy.util.OrganizationContext.getCurrentOrganizationCode;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MeetingPlanServiceImplement implements MeetingPlanService {

    private static final Logger logger = LoggerFactory.getLogger(MeetingPlanServiceImplement.class);

    private final MeetingPlanRepository meetingPlanRepository;
    private final MeetingSessionRepository meetingSessionRepository;
    private final PermissionService permissionService;
    private final MeetingPlanUserService meetingPlanUserService;
    private final MeetingPlanUserRepository meetingPlanUserRepository;
    private final NotificationService notificationService;
    private final ModelMapper modelMapper;

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${spring.mail.username}")
    private String systemEmail;

    @Value("${app.endpoint.client}")
    private String clientEndpoint;

    @Override
    public Page<MeetingPlanDTO> getMeetingPlansByCreatorId(String userId, Pageable pageable, String search) {
        // default sort by created date
        if (pageable.getSort().isUnsorted()) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(MeetingPlan_.CREATED_STAMP).ascending());
        }

        if (search != null && !search.isEmpty()) {
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

        if (search != null && !search.isEmpty()) {
            search = "( " + search + " ) AND memberId:" + userId;
        } else {
            search = "memberId:" + userId;
        }
        search += " AND statusId!" + MeetingPlanStatusEnum.PLAN_DRAFT.getStatusId();

        GenericSpecificationsBuilder<MeetingPlan> builder = new GenericSpecificationsBuilder<>();
        var specs = builder.build(new CriteriaParser().parse(search), MeetingPlanSpecification::new);
        Page<MeetingPlan> meetingPlans = meetingPlanRepository.findAll(specs, pageable);

        List<UUID> planIds = meetingPlans.getContent().stream()
            .map(MeetingPlan::getId)
            .collect(Collectors.toList());

        List<MeetingPlanUser> assignments = meetingPlanUserRepository.findAssignmentsForUserInPlans(userId, planIds);
        Map<UUID, MeetingSessionDTO> assignmentMap = assignments.stream()
            .filter(mpu -> mpu.getMeetingSession() != null)
            .collect(Collectors.toMap(
                mpu -> mpu.getMeetingPlan().getId(),
                mpu -> modelMapper.map(mpu.getMeetingSession(), MeetingSessionDTO.class)
            ));

        List<Object[]> userResults = meetingPlanUserRepository.findAllUsersByPlanIds(planIds);
        Map<UUID, List<User>> userMap = userResults.stream()
            .collect(Collectors.groupingBy(
                row -> (UUID) row[0],
                Collectors.mapping(row -> (User) row[1], Collectors.toList())
            ));

        return meetingPlans.map(meetingPlan -> {
            MeetingPlanDTO dto = convertToDto(meetingPlan);
            dto.setAssignedSession(assignmentMap.get(meetingPlan.getId()));
            dto.setMembers(userMap.getOrDefault(meetingPlan.getId(), Collections.emptyList()));
            return dto;
        });
    }

    @Override
    public MeetingPlan getMeetingPlanById(String userId, UUID id) {
        return permissionService.checkMeetingPlanCreatorOrMember(userId, id);
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
        sendUpdateNotifications(members, userId, mp);
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

        MeetingPlanStatusEnum currentStatus = MeetingPlanStatusEnum.fromStatusId(plan.getStatus().getStatusId());
        MeetingPlanStatusEnum newStatus = MeetingPlanStatusEnum.fromStatusId(statusId);

        Map<MeetingPlanStatusEnum, Set<MeetingPlanStatusEnum>> transitions = Map.of(MeetingPlanStatusEnum.PLAN_DRAFT,
            Set.of(MeetingPlanStatusEnum.PLAN_REG_OPEN, MeetingPlanStatusEnum.PLAN_CANCELED),
            MeetingPlanStatusEnum.PLAN_REG_OPEN, Set.of(MeetingPlanStatusEnum.PLAN_CANCELED),
            MeetingPlanStatusEnum.PLAN_REG_CLOSED,
            Set.of(MeetingPlanStatusEnum.PLAN_ASSIGNED, MeetingPlanStatusEnum.PLAN_CANCELED),
            MeetingPlanStatusEnum.PLAN_ASSIGNED, Set.of(MeetingPlanStatusEnum.PLAN_CANCELED),
            MeetingPlanStatusEnum.PLAN_IN_PROGRESS, Set.of(MeetingPlanStatusEnum.PLAN_CANCELED));

        if (!transitions.containsKey(currentStatus) || !transitions.get(currentStatus).contains(newStatus)) {
            throw new ApiException(ErrorCode.INVALID_STATUS_TRANSITION,
                "Invalid status transition from " + currentStatus.getStatusId() + " to " + newStatus.getStatusId());
        }
        meetingPlanRepository.updateStatus(planId, statusId);

        sendStatusNotifications(userId, plan, statusId);
    }

    @Override
    @Transactional
    public void closeRegistrations(Date now) {
        entityManager.joinTransaction();

        List<MeetingPlan> plansToClose = meetingPlanRepository
            .findByStatusIdAndRegistrationDeadlineBefore(MeetingPlanStatusEnum.PLAN_REG_OPEN.getStatusId(), now);
        if (!plansToClose.isEmpty()) {
            for (MeetingPlan plan : plansToClose) {
                plan.setStatusId(MeetingPlanStatusEnum.PLAN_REG_CLOSED.getStatusId());
            }
            meetingPlanRepository.saveAll(plansToClose);
        }
    }

    @Override
    @Transactional
    public void startMeetingPlans(Date now) {
        entityManager.joinTransaction();

        List<MeetingPlan> plansToStart = meetingPlanRepository
            .findByStatusId(MeetingPlanStatusEnum.PLAN_ASSIGNED.getStatusId());
        List<UUID> planIds = plansToStart.stream()
            .map(MeetingPlan::getId)
            .collect(Collectors.toList());
        List<MeetingSession> sessions = planIds.isEmpty() ? List.of() :
            meetingSessionRepository.findEarliestByPlanIds(planIds);

        Map<UUID, MeetingSession> earliestSessions = sessions.stream()
            .collect(Collectors.toMap(
                MeetingSession::getPlanId,
                session -> session
            ));

        List<MeetingPlan> plansToUpdate = new ArrayList<>();
        for (MeetingPlan plan : plansToStart) {
            MeetingSession session = earliestSessions.get(plan.getId());
            if (session != null && session.getStartTime().before(now)) {
                plan.setStatusId(MeetingPlanStatusEnum.PLAN_IN_PROGRESS.getStatusId());
                plansToUpdate.add(plan);
            }
        }
        if (!plansToUpdate.isEmpty()) {
            meetingPlanRepository.saveAll(plansToUpdate);
        }
    }

    @Override
    @Transactional
    public void completeMeetingPlans(Date now) {
        entityManager.joinTransaction();

        List<MeetingPlan> plansInProgress = meetingPlanRepository
            .findByStatusId(MeetingPlanStatusEnum.PLAN_IN_PROGRESS.getStatusId());
        List<UUID> planIds = plansInProgress.stream()
            .map(MeetingPlan::getId)
            .collect(Collectors.toList());
        List<MeetingSession> latestSessions = planIds.isEmpty() ? List.of() :
            meetingSessionRepository.findLatestByPlanIds(planIds);

        Map<UUID, MeetingSession> sessionMap = latestSessions.stream()
            .collect(Collectors.toMap(
                MeetingSession::getPlanId,
                session -> session
            ));

        List<MeetingPlan> plansToUpdate = new ArrayList<>();
        for (MeetingPlan plan : plansInProgress) {
            MeetingSession session = sessionMap.get(plan.getId());
            if (session != null && session.getEndTime().before(now)) {
                plan.setStatusId(MeetingPlanStatusEnum.PLAN_COMPLETED.getStatusId());
                plansToUpdate.add(plan);
            }
        }
        if (!plansToUpdate.isEmpty()) {
            meetingPlanRepository.saveAll(plansToUpdate);
        }
    }

    private MeetingPlanDTO convertToDto(MeetingPlan meetingPlan) {
        return modelMapper.map(meetingPlan, MeetingPlanDTO.class);
    }

    private void sendUpdateNotifications(List<User> members, String userId, MeetingPlan mp) {
        for (User member : members) {
            try {
                notificationService.createInAppNotification(userId, member.getId(),
                    "Cuộc họp \"" + mp.getName() + "\" vừa được cập nhật.",
                    "/meetings/joined-meetings/" + mp.getId());
            } catch (Exception e) {
                logger.error("Failed to send update notification to user {} for meeting plan {}: {}", member.getId(),
                    mp.getId(), e.getMessage());
            }
        }
    }

    private void sendStatusNotifications(String userId, MeetingPlan plan, String statusId) {
        List<User> members = meetingPlanUserService.getAllMeetingPlanUsers(userId, plan.getId());
        Map<String, Object> model = new HashMap<>();
        String link = clientEndpoint + "/meetings/joined-meetings/" + plan.getId();
        String creatorName = (plan.getCreator().getFirstName() != null ? plan.getCreator().getFirstName() : "") + " "
            + (plan.getCreator().getLastName() != null ? plan.getCreator().getLastName() : "");
        model.put("meetingCreator", creatorName.trim().isEmpty() ? plan.getCreatedBy() : creatorName.trim());
        model.put("meetingName", plan.getName());
        model.put("location", plan.getLocation());
        model.put("meetingDescription", plan.getDescription());
        model.put("meetingLink", link);

        for (User member : members) {
            String subject;
            String mailForm;

            switch (statusId) {
                case "PLAN_REG_OPEN" -> {
                    subject = "Bạn được thêm vào cuộc họp \"" + plan.getName() + "\".";
                    mailForm = "new-meeting";
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy, HH:mm");
                    model.put("registrationDeadline", formatter.format(plan.getRegistrationDeadline()));
                }
                case "PLAN_ASSIGNED" -> {
                    MeetingSessionDTO sessionDto = meetingPlanUserService.getMyAssignment(member.getId(), plan.getId());
                    if (sessionDto != null) {
                        String formattedDate = new SimpleDateFormat("dd/MM/yyyy, 'từ' HH:mm")
                            .format(sessionDto.getStartTime()) + " - "
                            + new SimpleDateFormat("HH:mm").format(sessionDto.getEndTime());
                        subject = "Bạn đã được phân công vào phiên họp vào ngày " + formattedDate + " trong cuộc họp \""
                            + plan.getName() + "\".";
                        mailForm = "meeting-session-assign";
                        model.put("assignedSession", formattedDate);
                    } else {
                        continue;
                    }
                }
                case "PLAN_CANCELED" -> {
                    subject = "Cuộc họp \"" + plan.getName() + "\" đã bị hủy.";
                    mailForm = "cancel-meeting";
                }
                default -> {
                    continue;
                }
            }

            try {
                notificationService.createInAppNotification(userId, member.getId(), subject, link);
                if (member.getEmail() != null) {
                    String memberName = (member.getFirstName() != null ? member.getFirstName() : "") + " "
                        + (member.getLastName() != null ? member.getLastName() : "");
                    model.put("userName", memberName.trim().isEmpty() ? member.getId() : memberName.trim());
                    notificationService.createMailNotification(plan.getCreator().getEmail(), member.getEmail(), subject,
                        mailForm, model);
                }
            } catch (Exception e) {
                logger.error("Failed to send notification to user {} for meeting plan {} (status: {}): {}",
                    member.getId(), plan.getId(), statusId, e.getMessage());
            }
        }
    }
}
