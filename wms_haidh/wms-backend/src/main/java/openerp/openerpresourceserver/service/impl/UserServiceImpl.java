package openerp.openerpresourceserver.service.impl;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.entity.User;
import openerp.openerpresourceserver.repository.UserRepository;
import openerp.openerpresourceserver.service.UserService;

@AllArgsConstructor(onConstructor_ = @Autowired)
@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepo;

    @Override
    public List<User> getAllUsers() {
        List<User> users = userRepo.findAll();
        return users;
    }

    @Override
    public User getUserById(String id) {
        Optional<User> user = userRepo.findById(id);

        if (user.isEmpty()) {
            throw new NoSuchElementException("Not exist user with id " + id);
        }
        return user.get();
    }

    @Override
    public void synchronizeUser(String userId, String email, String firstName, String lastName) {
        User user = userRepo.findById(userId).orElse(null);

        if (user == null) {
            userRepo.save(User.builder()
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

            userRepo.save(user);
        }
    }

}
