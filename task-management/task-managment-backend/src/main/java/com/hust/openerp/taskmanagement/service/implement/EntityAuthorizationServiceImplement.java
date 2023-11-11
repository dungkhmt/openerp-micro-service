package com.hust.openerp.taskmanagement.service.implement;

import com.hust.openerp.taskmanagement.entity.EntityAuthorization;
import com.hust.openerp.taskmanagement.repository.EntityAuthorizationRepository;
import com.hust.openerp.taskmanagement.service.EntityAuthorizationService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor(onConstructor_ = @Autowired)
@Service
public class EntityAuthorizationServiceImplement implements EntityAuthorizationService {

    private EntityAuthorizationRepository entityAuthorizationRepo;

    @Override
    public Set<String> getEntityAuthorization(String id, List<String> roleIds) {
        List<EntityAuthorization> entityAuthorizations = entityAuthorizationRepo.findAllByIdStartingWithAndRoleIdIn(id,
            roleIds);

        return entityAuthorizations != null
            ? entityAuthorizations.stream().map(EntityAuthorization::getId).collect(Collectors.toSet())
            : Collections.emptySet();
    }
}
