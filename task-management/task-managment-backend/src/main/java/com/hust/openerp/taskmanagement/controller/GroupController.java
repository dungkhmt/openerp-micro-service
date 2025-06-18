package com.hust.openerp.taskmanagement.controller;

import com.hust.openerp.taskmanagement.config.OpenApiConfig;
import com.hust.openerp.taskmanagement.dto.GroupDTO;
import com.hust.openerp.taskmanagement.dto.form.GroupForm;
import com.hust.openerp.taskmanagement.service.GroupService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/groups")
@Tag(name = "Group", description = "APIs for group management")
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @GetMapping
    public List<GroupDTO> getGroupsByMe(Principal principal) {
        return groupService.getGroupsByUserId(principal.getName());
    }

    @GetMapping("/{id}")
    public GroupDTO getGroupById(Principal principal, @PathVariable UUID id) {
        return groupService.getGroupById(principal.getName(), id);
    }

    @PostMapping
    public GroupDTO createGroup(Principal principal, @RequestBody @Valid GroupForm form) {
        return groupService.createGroup(principal.getName(), form);
    }

    @PutMapping("/{id}")
    public GroupDTO updateGroup(Principal principal,
                                @PathVariable UUID id,
                                @RequestBody @Valid GroupForm form) {
        return groupService.updateGroup(principal.getName(), id, form);
    }
}

