package openerp.openerpresourceserver.controller;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.entity.User;
import openerp.openerpresourceserver.log.entity.LmsLog;
import openerp.openerpresourceserver.log.model.LmsLogModelCreate;
import openerp.openerpresourceserver.log.service.LmsLogService;
import openerp.openerpresourceserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    private LmsLogService lmsLogService;
    private UserService userService;

    /**
     * If the user is not in the database, add them. If they are in the database, update their
     * information to synchronize with Keycloak
     *
     * @param token The JWT token that was passed in the request.
     */
    @GetMapping
    public void syncUser(JwtAuthenticationToken token) {
        Jwt principal = (Jwt) token.getPrincipal();
        userService.synchronizeUser(
                principal.getClaim("preferred_username"),
                principal.getClaim("email"),
                principal.getClaim("given_name"),
                principal.getClaim("family_name"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable String id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok().body(user);
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllUsers(Principal principal) {
        List<User> users = userService.getAllUsers();
        //LmsLogModelCreate log = new LmsLogModelCreate();
        //log.setUserId(principal.getName());
        //log.setActionType(LmsLog.ACTION_TYPE_GET_ALL_USERS);
        //log.setDescription("Get all users of the system");
        //lmsLogService.save(log);

        return ResponseEntity.ok().body(users);
    }
}
