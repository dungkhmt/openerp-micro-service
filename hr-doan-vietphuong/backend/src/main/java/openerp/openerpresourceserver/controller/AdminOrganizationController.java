package openerp.openerpresourceserver.controller;

import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.dto.request.organization.OrganizationQueryRequest;
import openerp.openerpresourceserver.dto.request.organization.OrganizationRequest;
import openerp.openerpresourceserver.dto.response.Result;
import openerp.openerpresourceserver.exception.BadRequestException;
import openerp.openerpresourceserver.service.OrganizationService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/organizations")
@Validated
public class AdminOrganizationController {
    private final OrganizationService organizationService;

    @GetMapping
    public Result getOrganizationsByProperties(OrganizationQueryRequest dto) {
        return Result.ok(organizationService.getOrganizationsByProperties(dto));
    }

    @GetMapping("/{id}")
    public Result getOrganizationById(@PathVariable Long id) {
        return Result.ok(organizationService.getOrganizationById(id));
    }

    @PostMapping
    public Result addOrganization(@RequestBody OrganizationRequest dto) throws BadRequestException {
        return Result.ok(organizationService.addOrganization(dto));
    }

    @PutMapping
    public Result updateOrganization(@RequestBody OrganizationRequest dto) throws BadRequestException {
        return Result.ok(organizationService.updateOrganization(dto));
    }

    @DeleteMapping("/{id}")
    public Result deleteOrganizations(@PathVariable Long id) {
        return Result.ok(organizationService.deleteOrganizations(id));
    }
}
