package openerp.openerpresourceserver.config.security;

import openerp.openerpresourceserver.entity.Employee;
import openerp.openerpresourceserver.enums.StatusEnum;
import openerp.openerpresourceserver.repo.EmployeeRepository;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.stream.Collectors;

public class Jwt2AuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter;
    private final EmployeeRepository employeeRepository;
    private String principalClaimName = "preferred_username";

    public Jwt2AuthenticationConverter(EmployeeRepository employeeRepository) {
        this.jwtGrantedAuthoritiesConverter = new Jwt2AuthoritiesConverter();
        this.employeeRepository = employeeRepository;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = this.extractAuthorities(jwt);
        String userLoginId = jwt.getClaimAsString(this.principalClaimName);

        if (userLoginId == null) {
            throw new IllegalArgumentException("Preferred username claim is missing in JWT");
        }
        // Tra cứu người dùng từ cơ sở dữ liệu
        Employee employee = employeeRepository.findByUserIdAndStatus(userLoginId, StatusEnum.ACTIVE.ordinal())
                .orElseThrow(() -> new IllegalArgumentException("User with user login id " + userLoginId + " not found or inactive"));

        Collection<GrantedAuthority> userAuthorities = employee.getPosition().getRoles()
                .stream()
                .map(role -> "ROLE_" + role.getName())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        authorities.addAll(userAuthorities);
        // Tạo CustomJwtAuthenticationToken với thông tin từ entity
        return new CustomJwtAuthenticationToken(
                jwt,
                authorities,
                employee.getEmail(),
                employee.getEmployeeId()
        );
    }

    protected Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        return jwtGrantedAuthoritiesConverter.convert(jwt);
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