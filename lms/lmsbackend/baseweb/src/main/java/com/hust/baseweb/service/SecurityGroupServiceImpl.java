package com.hust.baseweb.service;

import com.hust.baseweb.entity.SecurityGroup;
import com.hust.baseweb.model.GetAllRolesOM;
import com.hust.baseweb.repo.SecurityGroupRepo;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class SecurityGroupServiceImpl implements SecurityGroupService {

    SecurityGroupRepo securityGroupRepo;

    @Override
    public List<SecurityGroup> findAll() {
        return securityGroupRepo.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Set<GetAllRolesOM> getRoles() {
        Set<GetAllRolesOM> roles = securityGroupRepo.getRoles();

        if (null == roles) {
            return new HashSet<>();
        }

        roles.removeIf(role -> StringUtils.isBlank(role.getName()));
        return roles;
    }
}
