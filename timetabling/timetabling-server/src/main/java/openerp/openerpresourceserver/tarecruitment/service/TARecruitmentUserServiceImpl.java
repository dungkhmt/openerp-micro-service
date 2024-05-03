package openerp.openerpresourceserver.tarecruitment.service;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.model.entity.User;
import openerp.openerpresourceserver.repo.UserRepo;
import openerp.openerpresourceserver.tarecruitment.dto.UserInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@AllArgsConstructor(onConstructor_ = @Autowired)
@Service
public class TARecruitmentUserServiceImpl implements TARecruitment_UserService {

    private UserRepo userRepo;

    @Override
    public UserInfoDTO getUserInfo(String userId) {
        Optional<User> user = userRepo.findById(userId);
        if(user.isEmpty()) {
            throw new IllegalArgumentException("User did not exist");
        }
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setEmail(user.get().getEmail());
        String userName = user.get().getFirstName() + user.get().getLastName();
        userInfoDTO.setName(userName);
        return userInfoDTO;
    }
}
