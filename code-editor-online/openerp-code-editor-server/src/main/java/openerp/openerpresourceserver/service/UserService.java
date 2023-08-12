package openerp.openerpresourceserver.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.dto.UserDTO;
import openerp.openerpresourceserver.entity.User;
import openerp.openerpresourceserver.repo.UserRepository;
import openerp.openerpresourceserver.service.mapper.UserMapper;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    public Page<UserDTO> search(String keyword, Pageable pageable) {
        return userRepository.findByIdLikeOrFirstNameLikeOrLastNameLike(keyword, keyword, keyword, pageable)
                .map(userMapper::toDTO);
    }
    public void synchronizeUser(String userId, String email, String firstName, String lastName) {
        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            userRepository.save(User.builder()
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

            userRepository.save(user);
        }
    }

}
