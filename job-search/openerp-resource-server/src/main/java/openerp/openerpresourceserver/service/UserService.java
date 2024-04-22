package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.entity.User;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();

    User getUserById(String id);

    void synchronizeUser(String userId, String email, String firstName, String lastName);

    User getUserInfoFromToken(JwtAuthenticationToken token);
}
