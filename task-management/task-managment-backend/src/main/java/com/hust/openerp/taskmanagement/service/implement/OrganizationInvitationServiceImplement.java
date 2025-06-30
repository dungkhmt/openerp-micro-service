package com.hust.openerp.taskmanagement.service.implement;

import com.hust.openerp.taskmanagement.dto.OrganizationInvitationDTO;
import com.hust.openerp.taskmanagement.dto.form.OrganizationInvitationForm;
import com.hust.openerp.taskmanagement.entity.Organization;
import com.hust.openerp.taskmanagement.entity.OrganizationInvitation;
import com.hust.openerp.taskmanagement.entity.OrganizationUser;
import com.hust.openerp.taskmanagement.entity.User;
import com.hust.openerp.taskmanagement.exception.ApiException;
import com.hust.openerp.taskmanagement.exception.ErrorCode;
import com.hust.openerp.taskmanagement.model.OrgInvitationStatusEnum;
import com.hust.openerp.taskmanagement.repository.OrganizationInvitationRepository;
import com.hust.openerp.taskmanagement.repository.OrganizationRepository;
import com.hust.openerp.taskmanagement.repository.OrganizationUserRepository;
import com.hust.openerp.taskmanagement.repository.UserRepository;
import com.hust.openerp.taskmanagement.service.NotificationService;
import com.hust.openerp.taskmanagement.service.OrganizationInvitationService;
import com.hust.openerp.taskmanagement.service.PermissionService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.hust.openerp.taskmanagement.model.DefaultTenantId.DEFAULT_TENANT_ID;
import static com.hust.openerp.taskmanagement.multitenancy.util.OrganizationContext.getCurrentOrganizationCode;
import static com.hust.openerp.taskmanagement.multitenancy.util.OrganizationContext.setCurrentOrganizationCode;

@Service
@RequiredArgsConstructor
public class OrganizationInvitationServiceImplement implements OrganizationInvitationService {

    private final OrganizationInvitationRepository organizationInvitationRepository;
    private final ModelMapper modelMapper;
    private final PermissionService permissionService;
    private final OrganizationRepository organizationRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final OrganizationUserRepository organizationUserRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${spring.mail.username}")
    private String systemEmail;

    @Value("${app.endpoint.client}")
    private String clientEndpoint;

    private static final Logger logger = LoggerFactory.getLogger(OrganizationInvitationServiceImplement.class);

    @Override
    @Transactional
    public void inviteUsers(String currentUserId, OrganizationInvitationForm form) {
        UUID organizationId = form.getOrganizationId();
        permissionService.checkOrganizationCreator(currentUserId, organizationId);

        Organization organization = organizationRepository.findById(organizationId).orElseThrow(() -> new ApiException(ErrorCode.ORGANIZATION_NOT_FOUND));

        for (OrganizationInvitationForm.Invitee invitee : form.getInvitees()) {
            String email = invitee.getEmail();
            boolean alreadyInvited = organizationInvitationRepository.existsByOrganizationIdAndEmailAndStatusId(organizationId, email, OrgInvitationStatusEnum.PENDING.getStatusId());
            if (alreadyInvited) {
                throw new ApiException(ErrorCode.ORGANIZATION_USER_INVITED);
            }

            Optional<User> user = userRepository.findByEmail(email);
            if (user.isPresent() && organizationUserRepository.existsByUserId(user.get().getId())) {
                throw new ApiException(ErrorCode.ORGANIZATION_USER_ADDED);
            }

            String token = UUID.randomUUID().toString();
            Instant now = Instant.now().truncatedTo(ChronoUnit.HOURS)
                .plus(1, ChronoUnit.HOURS);
            Date expiration = Date.from(now.plus(14, ChronoUnit.DAYS));

            OrganizationInvitation invitation = OrganizationInvitation.builder()
                .id(UUID.randomUUID())
                .email(email).invitedBy(currentUserId)
                .organizationId(organizationId)
                .roleId(invitee.getRoleId())
                .statusId(OrgInvitationStatusEnum.PENDING.getStatusId())
                .token(token)
                .expirationTime(expiration)
                .build();

            organizationInvitationRepository.save(invitation);

            User inviter = userRepository.findById(currentUserId).orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_EXIST));
            String firstName = inviter.getFirstName();
            String lastName = inviter.getLastName();
            String inviterName = (firstName == null ? "" : firstName) + (lastName == null ? "" : " " + lastName);

            sendInvitationEmail(email, organization.getName(), inviterName, invitee.getRoleId(), token, expiration);
        }
    }

    @Override
    public List<OrganizationInvitationDTO> getPendingInvitationsByUserId(String userId) {
        User user = userRepository.findById(userId).
            orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_EXIST));

        return organizationInvitationRepository.findByEmailAndStatusId(user.getEmail(), OrgInvitationStatusEnum.PENDING.getStatusId())
            .stream().map(orgInvitation -> modelMapper.map(orgInvitation, OrganizationInvitationDTO.class))
            .collect(Collectors.toList());
    }

    @Override
    public OrganizationInvitationDTO validateToken(String token, String currentUserId) {
        OrganizationInvitation invitation = organizationInvitationRepository.findByToken(token)
            .orElseThrow(() -> new ApiException(ErrorCode.INVALID_INVITATION_TOKEN));
        if (invitation.getExpirationTime().before(new Date())) {
            throw new ApiException(ErrorCode.INVITATION_EXPIRED);
        }
        if (!invitation.getStatusId().equals(OrgInvitationStatusEnum.PENDING.getStatusId())) {
            throw new ApiException(ErrorCode.INVITATION_ALREADY_HANDLED);
        }

        User currentUser = userRepository.findById(currentUserId).
            orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_EXIST));

        if (!invitation.getEmail().equalsIgnoreCase(currentUser.getEmail())) {
            throw new ApiException(ErrorCode.ACCESS_DENIED);
        }

        return modelMapper.map(invitation, OrganizationInvitationDTO.class);
    }

    @Override
    @Transactional
    public void acceptInvitation(String token, String currentUserId) {
        this.validateToken(token, currentUserId);
        OrganizationInvitation invitation = organizationInvitationRepository.
            findByToken(token).orElseThrow(() -> new ApiException(ErrorCode.INVALID_INVITATION_TOKEN));

        invitation.setStatusId(OrgInvitationStatusEnum.ACCEPTED.getStatusId());
        organizationInvitationRepository.save(invitation);

        organizationUserRepository.insertOrganizationUser(
            invitation.getOrganizationId(),
            DEFAULT_TENANT_ID.getId(),
            currentUserId,
            new Date(),
            invitation.getRoleId(),
            null
        );
    }

    @Override
    @Transactional
    public void declineInvitation(String token, String currentUserId) {
        this.validateToken(token, currentUserId);
        OrganizationInvitation invitation = organizationInvitationRepository.
            findByToken(token).orElseThrow(() -> new ApiException(ErrorCode.INVALID_INVITATION_TOKEN));

        invitation.setStatusId(OrgInvitationStatusEnum.DECLINED.getStatusId());
        organizationInvitationRepository.save(invitation);
    }

    @Override
    public List<OrganizationInvitationDTO> getPendingInvitationsByOrgId(String currentUserId, UUID id) {
        return organizationInvitationRepository.
            findByStatusId(OrgInvitationStatusEnum.PENDING.getStatusId()).
            stream().map(obj -> modelMapper.map(obj, OrganizationInvitationDTO.class)).
            collect(Collectors.toList());
    }

    private void sendInvitationEmail(String email, String organizationName, String inviterName, String roleName, String token, Date expirationDate) {
        String link = clientEndpoint + "/invitations/" + token;

        Map<String, Object> model = new HashMap<>();
        model.put("userEmail", email);
        model.put("organizationName", organizationName);
        model.put("inviterName", inviterName);
        model.put("roleName", roleName);
        model.put("link", link);

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        model.put("expirationDate", formatter.format(expirationDate));

        String message = "Bạn đã được mời tham gia tổ chức \"" + organizationName + "\".";

        try {
            notificationService.createMailNotification(systemEmail, email, message, "organization-invitation", model);
        } catch (Exception e) {
            logger.error("Failed to send invitation email to {} for organization {}: {}", email, organizationName, e.getMessage(), e);
        }
    }

    @Transactional
    public void findAndExpireInvitations() {
        entityManager.joinTransaction();

        Date now = new Date();
        List<OrganizationInvitation> expired = organizationInvitationRepository
            .findByStatusIdAndExpirationTimeBefore(OrgInvitationStatusEnum.PENDING.getStatusId(), now);

        if (!expired.isEmpty()) {
            expired.forEach(invite -> invite.setStatusId(OrgInvitationStatusEnum.EXPIRED.getStatusId()));
            organizationInvitationRepository.saveAll(expired);
        }
    }
}

