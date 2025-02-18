package com.hust.baseweb.controller;


import com.hust.baseweb.service.UserService;
import io.lettuce.core.dynamic.annotation.Param;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@CrossOrigin
@AllArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApiController {

    UserService userService;


    /**
     * If the user is not in the database, add them. If they are in the database, update their
     * information to synchronize with Keycloak
     *
     * @param token The JWT token that was passed in the request.
     */
    @GetMapping("/")
    public void syncUser(JwtAuthenticationToken token) {
        Jwt principal = (Jwt) token.getPrincipal();
        userService.synchronizeUser(
            principal.getClaim("preferred_username"),
            principal.getClaim("email"),
            principal.getClaim("given_name"),
            principal.getClaim("family_name"));
    }

//    @GetMapping("screen-security")
//    public ResponseEntity<?> getScrSecurInfo(Principal principal) {
//        return ResponseEntity.ok().body(applicationService.getScrSecurInfo(principal.getName()));
//    }

    /**
     * This function is used to search for users by their name or email
     *
     * @param pageable This is a parameter that is used to paginate the results.
     * @param keyword  the keyword to search for
     * @return
     */
    @GetMapping("/search-user")
    public ResponseEntity<?> searchUser(
        Pageable pageable,
        @Param("keyword") String keyword
    ) {
        if (keyword == null) {
            keyword = "";
        }

        return ResponseEntity.ok().body(userService.searchUser(pageable, keyword));
    }

}

