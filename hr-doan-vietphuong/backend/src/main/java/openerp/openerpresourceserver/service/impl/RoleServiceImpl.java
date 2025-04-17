package openerp.openerpresourceserver.service.impl;

import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.dto.request.PagingRequest;
import openerp.openerpresourceserver.dto.request.role.RoleQueryRequest;
import openerp.openerpresourceserver.dto.request.role.RoleRequest;
import openerp.openerpresourceserver.entity.Position;
import openerp.openerpresourceserver.entity.Role;
import openerp.openerpresourceserver.enums.RoleEnum;
import openerp.openerpresourceserver.enums.StatusEnum;
import openerp.openerpresourceserver.exception.BadRequestException;
import openerp.openerpresourceserver.exception.NotFoundException;
import openerp.openerpresourceserver.repo.PositionRepository;
import openerp.openerpresourceserver.repo.RoleRepository;
import openerp.openerpresourceserver.repo.specification.RoleSpecification;
import openerp.openerpresourceserver.service.RoleService;
import openerp.openerpresourceserver.util.SecurityUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final PositionRepository positionRepository;

    @Override
    public Page<Role> getRolesByProperties(final RoleQueryRequest dto, final PagingRequest pagingRequest) {
        Pageable pageable = PageRequest.of(pagingRequest.getPage(), pagingRequest.getSize(), Sort.by(Sort.Direction.DESC, "id"));
        Specification<Role> specification = Specification
                .where(RoleSpecification.hasKeyword(dto.getKeyword()))
                .and(RoleSpecification.hasStatus(dto.getStatus()));
        return roleRepository.findAll(specification, pageable);
    }

    @Override
    public Role getRoleById(final Long id) {
        Optional<Role> roleOptional = roleRepository.findById(id);
        if (roleOptional.isEmpty()) {
            throw new NotFoundException("Role not found");
        }
        return roleOptional.get();
    }

    @Override
    public Role addRole(final RoleRequest dto) throws BadRequestException {
        if (roleRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new BadRequestException("Role name already exists");
        }
        Role role = Role.builder()
                .name(dto.getName().toUpperCase())
                .status(dto.getStatus())
                .updatedBy(SecurityUtil.getUserEmail())
                .build();
        return roleRepository.save(role);
    }

    @Override
    public Role updateRole(final RoleRequest dto) throws BadRequestException {
        if (roleRepository.existsByIdNotAndNameIgnoreCase(dto.getId(), dto.getName())) {
            throw new BadRequestException("Role name already exists");
        }
        Optional<Role> roleOptional = roleRepository.findById(dto.getId());
        if (roleOptional.isEmpty()) {
            throw new NotFoundException("Role not found");
        }
        Role role = roleOptional.get();
        role.setName(dto.getName().toUpperCase());
        role.setStatus(dto.getStatus());
        role.setUpdatedBy(SecurityUtil.getUserEmail());
        return roleRepository.save(role);
    }

    @Override
    public List<Role> deleteRoles(final List<Long> roleIdList) throws BadRequestException {
        List<Role> result = new ArrayList<>();
        for (long id : roleIdList) {
            Optional<Role> roleOptional = roleRepository.findById(id);
            if (roleOptional.isPresent()) {
                Role role = roleOptional.get();
                if (role.getName().equals(RoleEnum.ADMIN.name())) {
                    throw new BadRequestException("Cannot delete ADMIN role");
                }
                List<Position> positions = positionRepository.findByRolesContaining(role);
                for (Position position : positions) {
                    position.getRoles().remove(role);
                    positionRepository.save(position);
                }
                role.setStatus(StatusEnum.INACTIVE.ordinal());
                role.setUpdatedBy(SecurityUtil.getUserEmail());
                result.add(roleRepository.save(role));
            } else {
                throw new NotFoundException("Role not found");
            }
        }
        return result;
    }
}
