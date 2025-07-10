package com.hust.wmsbackend.management.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Jwt2AuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Map<String, Object> realmAccess = (Map<String, Object>) jwt.getClaims().getOrDefault("realm_access", Collections.emptyMap());
        Collection<String> realmRoles = (Collection<String>) realmAccess.getOrDefault("roles", Collections.emptyList());

        Map<String, Object> resourceAccess = (Map<String, Object>) jwt.getClaims().getOrDefault("resource_access", Collections.emptyMap());

        // We assume here you have "openerp-ui-dev" client configured with "client roles" mapper in Keycloak
        Map<String, Object> publicClientAccess = (Map<String, Object>) resourceAccess
            .getOrDefault("openerp-ui-dev", Collections.emptyMap());
        Collection<String> publicClientRoles = (Collection<String>) publicClientAccess.getOrDefault(
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
