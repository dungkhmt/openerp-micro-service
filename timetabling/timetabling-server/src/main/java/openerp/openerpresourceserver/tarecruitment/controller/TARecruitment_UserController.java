package openerp.openerpresourceserver.tarecruitment.controller;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.tarecruitment.entity.dto.UserInfoDTO;
import openerp.openerpresourceserver.tarecruitment.service.TARecruitment_UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@Log4j2
@AllArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/user")
public class TARecruitment_UserController {
    private TARecruitment_UserService TARecruitmentUserService;

    @GetMapping("/get-user-info")
    public ResponseEntity<?> getUserInfo(Principal principal) {
        try {
            String userId = principal.getName();
            UserInfoDTO userInfoDTO = TARecruitmentUserService.getUserInfo(userId);
            return ResponseEntity.ok().body(userInfoDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }
}
