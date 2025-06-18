package com.hust.openerp.taskmanagement.controller;

import com.hust.openerp.taskmanagement.config.OpenApiConfig;
import com.hust.openerp.taskmanagement.dto.GroupUserDTO;
import com.hust.openerp.taskmanagement.dto.form.GroupUserForm;
import com.hust.openerp.taskmanagement.service.GroupUserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/groups/{id}/users")
@Tag(name = "Group User", description = "APIs for managing users in a group")
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
@RequiredArgsConstructor
public class GroupUserController {

    private final GroupUserService groupUserService;

    @GetMapping
    public List<GroupUserDTO> getUsersByGroupId(Principal principal,
                                                @PathVariable UUID id) {
        return groupUserService.getUsersByGroupId(principal.getName(), id);
    }

    @PostMapping
    public void addUserToGroup(Principal principal,
                               @PathVariable UUID id,
                               @RequestBody @Valid GroupUserForm request) {
        groupUserService.addUserToGroup(principal.getName(), id, request);
    }

    @DeleteMapping("/{userId}")
    public void removeUserFromGroup(Principal principal,
                                    @PathVariable UUID id,
                                    @PathVariable String userId) {
        groupUserService.removeUserFromGroup(principal.getName(), id, userId);
    }
}

