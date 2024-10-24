package openerp.openerpresourceserver.userfeaturestore.controller;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.userfeaturestore.entity.UserFeatures;
import openerp.openerpresourceserver.userfeaturestore.service.UserFeaturesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@Log4j2
@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
public class UserFeatureController {
    private UserFeaturesService userFeaturesService;

    @GetMapping("/user-features/get-user-features")
    public ResponseEntity<?> getUserFeatures(Principal principal){
        List<UserFeatures> lst = userFeaturesService.findAll();
        return ResponseEntity.ok().body(lst);
    }
}
