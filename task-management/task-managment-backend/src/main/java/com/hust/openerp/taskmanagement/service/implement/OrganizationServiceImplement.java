package com.hust.openerp.taskmanagement.service.implement;

import com.hust.openerp.taskmanagement.dto.OrganizationDTO;
import com.hust.openerp.taskmanagement.dto.form.OrganizationForm;
import com.hust.openerp.taskmanagement.dto.projection.OrganizationOverviewProjection;
import com.hust.openerp.taskmanagement.entity.Organization;
import com.hust.openerp.taskmanagement.entity.OrganizationUser;
import com.hust.openerp.taskmanagement.entity.User;
import com.hust.openerp.taskmanagement.exception.ApiException;
import com.hust.openerp.taskmanagement.exception.ErrorCode;
import com.hust.openerp.taskmanagement.model.RoleEnum;
import com.hust.openerp.taskmanagement.repository.OrganizationRepository;
import com.hust.openerp.taskmanagement.repository.OrganizationUserRepository;
import com.hust.openerp.taskmanagement.repository.UserRepository;
import com.hust.openerp.taskmanagement.service.OrganizationService;
import com.hust.openerp.taskmanagement.service.PermissionService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class OrganizationServiceImplement implements OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final PermissionService permissionService;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final OrganizationUserRepository organizationUserRepository;

    @Override
    public List<OrganizationDTO> getOrganizationsByUserId(String userId) {
        List<OrganizationOverviewProjection> overviews = organizationRepository.findOrganizationOverviewByUserId(userId);

        return overviews.stream().map(overview
            -> modelMapper.map(overview, OrganizationDTO.class)).toList();
    }

    @Override
    public OrganizationDTO getOrganizationById(String currentUserId, UUID id) {
        Organization org = permissionService.checkOrganizationMember(currentUserId, id);

        setLastOrganizationId(currentUserId, id);

        return modelMapper.map(org, OrganizationDTO.class);
    }

    @Override
    public OrganizationDTO createOrganization(String currentUserId, OrganizationForm request) {
        if (organizationRepository.existsByCode(request.getCode())) {
            throw new ApiException(ErrorCode.ORGANIZATION_CODE_EXIST);
        }

        Organization organization = Organization.builder()
            .id(UUID.randomUUID())
            .code(request.getCode())
            .name(request.getName())
            .createdBy(currentUserId)
            .build();

        organization = organizationRepository.save(organization);

        OrganizationUser organizationUser = OrganizationUser.builder()
            .organizationId(organization.getId())
            .userId(currentUserId)
            .fromDate(new Date())
            .roleId(RoleEnum.ADMIN.getRoleId())
            .build();
        organizationUserRepository.save(organizationUser);

        return modelMapper.map(organization, OrganizationDTO.class);
    }

    @Override
    public OrganizationDTO updateOrganization(String currentUserId, UUID id, OrganizationForm request) {
        Organization organization = permissionService.checkOrganizationCreator(currentUserId, id);

        organization.setName(request.getName());
        organization.setCode(request.getCode());

        organization = organizationRepository.save(organization);

        return modelMapper.map(organization, OrganizationDTO.class);
    }

    @Override
    public OrganizationDTO getLastOrganizationByUserId(String userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_EXIST));

        Organization org;
        if (user.getLastOrganizationId() != null) {
            org = organizationRepository.findById(user.getLastOrganizationId())
                .orElseThrow(() -> new ApiException(ErrorCode.LAST_ORGANIZATION_NOT_FOUND));
        } else {
            org = organizationRepository.findFirstOrganizationByUserId(user.getId())
                .orElseThrow(() -> new ApiException(ErrorCode.ORGANIZATION_NOT_FOUND));
        }
        return modelMapper.map(org, OrganizationDTO.class);
    }

    public void setLastOrganizationId(String userId, UUID id) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_EXIST));
        permissionService.checkOrganizationMember(userId, id);

        user.setLastOrganizationId(id);
        userRepository.save(user);
    }
}
