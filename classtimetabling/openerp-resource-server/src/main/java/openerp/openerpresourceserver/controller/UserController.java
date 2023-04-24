package openerp.openerpresourceserver.controller;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.entity.UserEntity;
import openerp.openerpresourceserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/user")
public class UserController {

    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable String id) {
        UserEntity user = userService.getUserById(id);
        return ResponseEntity.ok().body(user);
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllUsers() {
        List<UserEntity> users = userService.getAllUsers();
        return ResponseEntity.ok().body(users);
    }
}
