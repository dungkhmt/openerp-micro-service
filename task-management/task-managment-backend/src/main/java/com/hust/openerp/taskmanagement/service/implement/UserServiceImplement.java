package com.hust.openerp.taskmanagement.service.implement;

import com.hust.openerp.taskmanagement.entity.User;
import com.hust.openerp.taskmanagement.repository.UserRepository;
import com.hust.openerp.taskmanagement.service.UserService;
import lombok.AllArgsConstructor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            userLoginRepo.save(User.builder()
                    .id(userId)
                    .email(email)
                    .firstName(firstName)
                    .lastName(lastName)
                    .enabled(true)
                    .build());
        } else if (StringUtils.compareIgnoreCase(email, user.getEmail()) != 0 ||
                StringUtils.compareIgnoreCase(firstName, user.getFirstName()) != 0 ||
                StringUtils.compareIgnoreCase(lastName, user.getLastName()) != 0) {

            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);

            userLoginRepo.save(user);
        }
    }
}
