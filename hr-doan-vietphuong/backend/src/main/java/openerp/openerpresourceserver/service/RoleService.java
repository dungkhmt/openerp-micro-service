package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.dto.request.PagingRequest;
import openerp.openerpresourceserver.dto.request.role.RoleQueryRequest;
import openerp.openerpresourceserver.dto.request.role.RoleRequest;
import openerp.openerpresourceserver.entity.Role;
import openerp.openerpresourceserver.exception.BadRequestException;
import org.springframework.data.domain.Page;

import java.util.List;

public interface RoleService {
    Page<Role> getRolesByProperties(RoleQueryRequest dto, PagingRequest pagingRequest);

    Role getRoleById(Long id);

    Role addRole(RoleRequest dto) throws BadRequestException;

    Role updateRole(RoleRequest dto) throws BadRequestException;

    List<Role> deleteRoles(List<Long> roleIdList) throws BadRequestException;
}
