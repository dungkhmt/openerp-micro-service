package openerp.openerpresourceserver.controller;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.entity.Collector;
import openerp.openerpresourceserver.entity.Driver;
import openerp.openerpresourceserver.entity.Shipper;
import openerp.openerpresourceserver.entity.User;
import openerp.openerpresourceserver.repository.CollectorRepo;
import openerp.openerpresourceserver.repository.DriverRepo;
import openerp.openerpresourceserver.repository.ShipperRepo;
import openerp.openerpresourceserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final CollectorRepo collectorRepo;
    private final DriverRepo driverRepo;
    private final ShipperRepo shipperRepo;

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

    @GetMapping("get-collector/{username}")
    public ResponseEntity<?> getCollectorByUsername(@PathVariable String username) {
        Collector collector = collectorRepo.findByUsername(username);
        return ResponseEntity.ok().body(collector);
    }
    @GetMapping("get-driver/{username}")
    public ResponseEntity<?> getDriverByUsername(@PathVariable String username) {
        Driver driver = driverRepo.findByUsername(username);
        return ResponseEntity.ok().body(driver);
    }
    @GetMapping("get-shipper/{username}")
    public ResponseEntity<?> getShipperByUsername(@PathVariable String username) {
        Shipper shipper = shipperRepo.findByUsername(username);
        return ResponseEntity.ok().body(shipper);
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok().body(users);
    }

}
