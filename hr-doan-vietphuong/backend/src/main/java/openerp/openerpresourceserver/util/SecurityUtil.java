package openerp.openerpresourceserver.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.config.security.CustomJwtAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@UtilityClass
@Slf4j
public class SecurityUtil {

    public String getUserEmail() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof CustomJwtAuthenticationToken customAuth) {
            return customAuth.getEmail();
        }
        throw new IllegalStateException("Invalid authentication type");
    }

    public String getEmployeeId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof CustomJwtAuthenticationToken customAuth) {
            return customAuth.getEmployeeId();
        }
        throw new IllegalStateException("Invalid authentication type");
    }
}
