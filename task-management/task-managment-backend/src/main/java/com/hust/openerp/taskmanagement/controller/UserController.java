package com.hust.openerp.taskmanagement.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hust.openerp.taskmanagement.config.OpenApiConfig;
import com.hust.openerp.taskmanagement.dto.form.UpdateProfileForm;
import com.hust.openerp.taskmanagement.entity.User;
import com.hust.openerp.taskmanagement.service.UserService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "User", description = "APIs for user management")
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
public class UserController {
	private final UserService userService;

	@GetMapping
	public void syncUser(JwtAuthenticationToken token) {
		Jwt principal = (Jwt) token.getPrincipal();
		userService.synchronizeUser(principal.getClaim("preferred_username"), principal.getClaim("email"),
				principal.getClaim("given_name"), principal.getClaim("family_name"));
	}

	// FIXME: add role to @Secured annotation when role is defined
	@GetMapping("/users")
	public List<User> getAllUsers(@Nullable @RequestParam("q") String q) {
		return userService.searchUser(q);
	}

	@GetMapping("/users/me")
	public User getProfile(Principal principal) {
		return userService.findById(principal.getName());
	}

	@PutMapping("/users/me")
	public void updateProfile(Principal principal, @RequestBody UpdateProfileForm updateProfileForm) {
		userService.updateProfile(principal.getName(), updateProfileForm);
	}

	@GetMapping("/users/assigned-task-creator")
	public List<User> getMyCreatedTasks(Principal principal) {
		return userService.getUserCreateTaskAssignMe(principal.getName());
	}

	@GetMapping("/users/assigned-task-assignee")
	public List<User> getMyAssignedTasks(Principal principal) {
		return userService.getUserAssignTaskAssignMe(principal.getName());
	}

}
