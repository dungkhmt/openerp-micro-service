package com.hust.wmsbackend.management.controller;

import com.hust.wmsbackend.management.service.EntityAuthorizationService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@Validated
@AllArgsConstructor(onConstructor_ = @Autowired)
public class EntityAuthorizationController {

    private EntityAuthorizationService entityAuthorizationService;

    /**
     * > Get all the entity ids that the user has access to
     *
     * @param token The token that is passed in the request header.
     * @return A set of entity ids
     */
    @GetMapping("/entity-authorization/{id}")
    public ResponseEntity<Set<String>> getEntityAuthorization(@PathVariable String id, JwtAuthenticationToken token) {
        List<String> roleIds = token
                .getAuthorities()
                .stream()
                .filter(grantedAuthority -> !grantedAuthority
                        .getAuthority()
                        .startsWith("ROLE_GR")) // remove all composite roles
                .map(grantedAuthority -> { // convert role to permission
                    String roleId = grantedAuthority.getAuthority().substring(5); // remove prefix "ROLE_"
                    return roleId;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(entityAuthorizationService.getEntityAuthorization(id, roleIds));
    }
}
