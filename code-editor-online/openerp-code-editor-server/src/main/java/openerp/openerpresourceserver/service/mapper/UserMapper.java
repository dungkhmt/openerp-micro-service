package openerp.openerpresourceserver.service.mapper;

import org.springframework.stereotype.Component;

import openerp.openerpresourceserver.dto.UserDTO;
import openerp.openerpresourceserver.entity.User;

@Component
public class UserMapper {
    public UserDTO toDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        return userDTO;
    }
}
