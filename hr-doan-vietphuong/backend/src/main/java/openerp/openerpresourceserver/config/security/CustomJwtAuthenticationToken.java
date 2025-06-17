package openerp.openerpresourceserver.config.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;

public class CustomJwtAuthenticationToken extends JwtAuthenticationToken {
    private final String email;
    private final Integer employeeId;
    private final Long id;

    public CustomJwtAuthenticationToken(Jwt jwt, Collection<? extends GrantedAuthority> authorities,
                                        String email, Integer employeeId, Long id) {
        super(jwt, authorities, email);
        this.email = email;
        this.employeeId = employeeId;
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public Long getUserId() {
        return id;
    }
}
