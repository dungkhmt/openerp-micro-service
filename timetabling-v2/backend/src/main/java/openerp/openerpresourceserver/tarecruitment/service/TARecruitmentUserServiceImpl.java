package openerp.openerpresourceserver.tarecruitment.service;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.tarecruitment.entity.dto.UserInfoDTO;
import openerp.openerpresourceserver.tarecruitment.entity.TARecruitmentUser;
import openerp.openerpresourceserver.tarecruitment.repo.TARecruitmentUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@AllArgsConstructor(onConstructor_ = @Autowired)
@Service
public class TARecruitmentUserServiceImpl implements TARecruitment_UserService {

    private TARecruitmentUserRepo userRepo;

    @Override
    public UserInfoDTO getUserInfo(String userId) {
        Optional<TARecruitmentUser> user = userRepo.findById(userId);
        if(user.isEmpty()) {
            throw new IllegalArgumentException("User did not exist");
        }
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setEmail(user.get().getEmail());
        String userName = user.get().getFirstName() + " " + user.get().getLastName();
        userInfoDTO.setName(userName);
        return userInfoDTO;
    }
}
