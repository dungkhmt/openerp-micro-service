package com.hust.openerp.taskmanagement.service.implement;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hust.openerp.taskmanagement.dto.form.UpdateProfileForm;
import com.hust.openerp.taskmanagement.entity.User;
import com.hust.openerp.taskmanagement.repository.UserRepository;
import com.hust.openerp.taskmanagement.service.UserService;
import com.hust.openerp.taskmanagement.specification.UserSpecification;
import com.hust.openerp.taskmanagement.specification.builder.GenericSpecificationsBuilder;
import com.hust.openerp.taskmanagement.util.CriteriaParser;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Transactional
@jakarta.transaction.Transactional
public class UserServiceImplement implements UserService {
	private final UserRepository userLoginRepo;

	@Override
	public User findById(String userLoginId) {
		return userLoginRepo.findById(userLoginId).orElse(null);
	}

	@Override
	public java.util.List<User> getAllUsers() {
		return userLoginRepo.findAll();
	}

	@Override
	@Transactional
	public void synchronizeUser(String userId, String email, String firstName, String lastName) {
		User user = userLoginRepo.findById(userId).orElse(null);
		if (user == null) {
			userLoginRepo.save(User.builder().id(userId).email(email).firstName(firstName).lastName(lastName)
					.enabled(true).build());
		} else if (StringUtils.compareIgnoreCase(email, user.getEmail()) != 0
				|| StringUtils.compareIgnoreCase(firstName, user.getFirstName()) != 0
				|| StringUtils.compareIgnoreCase(lastName, user.getLastName()) != 0) {

			user.setEmail(email);
			user.setFirstName(firstName);
			user.setLastName(lastName);

			userLoginRepo.save(user);
		}
	}

	@Override
	public List<User> searchUser(@Nullable String q) {
		if (StringUtils.isBlank(q)) {
			return userLoginRepo.findAll();
		}

		var parser = new CriteriaParser();

		GenericSpecificationsBuilder<User> builder = new GenericSpecificationsBuilder<>();
		var spec = builder.build(parser.parse(q), UserSpecification::new);

		return userLoginRepo.findAll(spec);
	}

	@Override
	public List<User> getUserCreateTaskAssignMe(String userId) {
		return userLoginRepo.getAllUserCreateTaskAssignMe(userId);
	}

	@Override
	public List<User> getUserAssignTaskAssignMe(String userId) {
		return userLoginRepo.getAllUserAssignTaskAssignMe(userId);
	}

	@Override
	public void updateProfile(String userId, UpdateProfileForm updateProfileForm) {
		User user = userLoginRepo.findById(userId).orElse(null);
		if (updateProfileForm.getFirstName() != null && !updateProfileForm.getFirstName().isEmpty()) {
			user.setFirstName(updateProfileForm.getFirstName());
		}
		if (updateProfileForm.getLastName() != null && !updateProfileForm.getLastName().isEmpty()) {
			user.setLastName(updateProfileForm.getLastName());
		}
		if (updateProfileForm.getAvatarUrl() != null) {
			user.setAvatarUrl(updateProfileForm.getAvatarUrl());
		}
		userLoginRepo.save(user);
	}
}
