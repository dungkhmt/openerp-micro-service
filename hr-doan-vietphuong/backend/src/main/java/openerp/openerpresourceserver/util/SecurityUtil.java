package openerp.openerpresourceserver.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.config.security.CustomJwtAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@UtilityClass
@Slf4j
public class SecurityUtil {
    public Long getUserId() {
        return getCustomToken().getUserId();
    }

    public String getUserEmail() {
        return getCustomToken().getEmail();
    }

    public Integer getEmployeeId() {
        return getCustomToken().getEmployeeId();
    }

    private CustomJwtAuthenticationToken getCustomToken() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof CustomJwtAuthenticationToken customAuth) {
            return customAuth;
        }
        throw new IllegalStateException("Invalid authentication type");
    }
}
