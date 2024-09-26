package openerp.openerpresourceserver.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.dto.UserDTO;
import openerp.openerpresourceserver.service.UserService;

@RestController
@AllArgsConstructor
@RequestMapping("/code-editor/users")
public class UserController {

    private final UserService userService;

    @GetMapping(value = "/search")
    public ResponseEntity<Page<UserDTO>> search(@RequestParam(name = "keyword") String keyword, Pageable pageable) {
        return ResponseEntity.ok().body(userService.search("%" + keyword + "%", pageable));
    }

    @GetMapping(value = "/sync-user")
    public void syncUser(JwtAuthenticationToken token) {
        Jwt principal = (Jwt) token.getPrincipal();
        userService.synchronizeUser(
                principal.getClaim("preferred_username"),
                principal.getClaim("email"),
                principal.getClaim("given_name"),
                principal.getClaim("family_name"));
    }
}
