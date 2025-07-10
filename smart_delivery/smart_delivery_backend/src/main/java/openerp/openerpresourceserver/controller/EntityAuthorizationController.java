package openerp.openerpresourceserver.controller;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.service.EntityAuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class EntityAuthorizationController {

    private EntityAuthorizationService entityAuthorizationService;

    @Autowired
    public EntityAuthorizationController(EntityAuthorizationService entityAuthorizationService) {
        this.entityAuthorizationService = entityAuthorizationService;
    }
    /**
     * > Get all the entity ids that the user has access to
     *
     * @param token The token that is passed in the request header.
     * @return A set of entity ids
     */
    @GetMapping("/entity-authorization/{id}")
    public ResponseEntity<Set<String>> getEntityAuthorization(@PathVariable String id, JwtAuthenticationToken token) {
        System.out.println("idda2" + token.getAuthorities());

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

        System.out.println("idda" + roleIds);
        return ResponseEntity.ok().body(entityAuthorizationService.getEntityAuthorization(id, roleIds));
    }
}
