package openerp.openerpresourceserver.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.dto.UserDTO;
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

}
