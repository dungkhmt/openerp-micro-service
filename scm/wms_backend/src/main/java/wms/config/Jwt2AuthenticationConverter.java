package wms.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.util.Assert;

import java.util.Collection;

public class Jwt2AuthenticationConverter
        implements Converter<Jwt, AbstractAuthenticationToken> {

    private Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
    private String principalClaimName = "sub";

    public Jwt2AuthenticationConverter() {
    }

    public final AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = this.extractAuthorities(jwt);
        String principalClaimValue = jwt.getClaimAsString(this.principalClaimName);
        return new JwtAuthenticationToken(jwt, authorities, principalClaimValue);
    }

    /**
     * @deprecated
     */
    @Deprecated
    protected Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        return (Collection) this.jwtGrantedAuthoritiesConverter.convert(jwt);
    }

    public void setJwtGrantedAuthoritiesConverter(Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter) {
        Assert.notNull(jwtGrantedAuthoritiesConverter, "jwtGrantedAuthoritiesConverter cannot be null");
        this.jwtGrantedAuthoritiesConverter = jwtGrantedAuthoritiesConverter;
    }

    public void setPrincipalClaimName(String principalClaimName) {
        Assert.hasText(principalClaimName, "principalClaimName cannot be empty");
        this.principalClaimName = principalClaimName;
    }
}
