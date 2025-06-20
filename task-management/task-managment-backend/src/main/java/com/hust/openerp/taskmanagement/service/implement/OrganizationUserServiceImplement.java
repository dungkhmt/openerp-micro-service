package com.hust.openerp.taskmanagement.service.implement;

import com.hust.openerp.taskmanagement.dto.OrganizationUserDTO;
import com.hust.openerp.taskmanagement.entity.OrganizationUser;
import com.hust.openerp.taskmanagement.exception.ApiException;
import com.hust.openerp.taskmanagement.exception.ErrorCode;
import com.hust.openerp.taskmanagement.repository.OrganizationUserRepository;
import com.hust.openerp.taskmanagement.service.OrganizationUserService;
import com.hust.openerp.taskmanagement.service.PermissionService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class OrganizationUserServiceImplement implements OrganizationUserService {

    private final OrganizationUserRepository organizationUserRepository;
    private final PermissionService permissionService;
    private final ModelMapper modelMapper;

    @Override
    public List<OrganizationUserDTO> getUsersByOrganizationId(String userId, UUID id) {
        List<OrganizationUser> orgUsers = organizationUserRepository.findAllActive(id);
        return orgUsers.stream().map(orgUser -> modelMapper.map(orgUser, OrganizationUserDTO.class)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void removeUserFromOrganization(String currentUserId, UUID organizationId, String userId) {
        permissionService.checkOrganizationCreator(currentUserId, organizationId);

        OrganizationUser record = organizationUserRepository
            .findByUserId(userId)
            .orElseThrow(() -> new ApiException(ErrorCode.ORGANIZATION_USER_NOT_FOUND));
        if (record.getThrsDate() != null) {
            throw new ApiException(ErrorCode.ORGANIZATION_USER_DELETED);
        }

        record.setThrsDate(new Date());
        organizationUserRepository.save(record);
    }
}
