package wms.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This is a converter for roles as embedded in the JWT by a Keycloak server
 * Roles are taken from both realm_access.roles & resource_access.{client}.roles
 */
public class Jwt2AuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    /**
     * @param jwt
     * @return
     */
    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        final Map<String, Object> realmAccess = (Map<String, Object>) jwt
                .getClaims()
                .getOrDefault("realm_access", Collections.emptyMap());
        final Collection<String> realmRoles = (Collection<String>) realmAccess.getOrDefault("roles", Collections.emptyList());

        final Map<String, Object> resourceAccess = (Map<String, Object>) jwt
                .getClaims()
                .getOrDefault("resource_access", Collections.emptyMap());

        // We assume here you have "openerp-ui-dev" client configured with "client roles" mapper in Keycloak
        final Map<String, Object> publicClientAccess = (Map<String, Object>) resourceAccess
                .getOrDefault("openerp-ui-dev", Collections.emptyMap());
        final Collection<String> publicClientRoles = (Collection<String>) publicClientAccess.getOrDefault(
                "roles",
                Collections.emptyList());

        return Stream
                .concat(
                        realmRoles.stream(),
                        publicClientRoles.stream())
                .map(roleName -> "ROLE_" + roleName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
