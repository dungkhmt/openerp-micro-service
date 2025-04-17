package openerp.openerpresourceserver.controller;

import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.dto.request.PagingRequest;
import openerp.openerpresourceserver.dto.request.role.RoleQueryRequest;
import openerp.openerpresourceserver.dto.request.role.RoleRequest;
import openerp.openerpresourceserver.dto.response.Result;
import openerp.openerpresourceserver.dto.response.ResultMeta;
import openerp.openerpresourceserver.entity.Role;
import openerp.openerpresourceserver.exception.BadRequestException;
import openerp.openerpresourceserver.service.RoleService;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/roles")
@Validated
public class AdminRoleController {
    private final RoleService roleService;

    @GetMapping
    public Result getRolesByProperties(RoleQueryRequest dto, PagingRequest pagingRequest) {
        Page<Role> page = roleService.getRolesByProperties(dto, pagingRequest);
        return Result.ok(page.getContent(), ResultMeta.of(page));
    }

    @GetMapping("/{id}")
    public Result getRoleById(@PathVariable Long id) {
        return Result.ok(roleService.getRoleById(id));
    }

    @PostMapping
    public Result addRole(@RequestBody RoleRequest dto) throws BadRequestException {
        return Result.ok(roleService.addRole(dto));
    }

    @PutMapping
    public Result updateRole(@RequestBody RoleRequest dto) throws BadRequestException {
        return Result.ok(roleService.updateRole(dto));
    }

    @DeleteMapping
    public Result deleteRoles(@RequestBody List<Long> roleIdList) throws BadRequestException {
        return Result.ok(roleService.deleteRoles(roleIdList));
    }
}
