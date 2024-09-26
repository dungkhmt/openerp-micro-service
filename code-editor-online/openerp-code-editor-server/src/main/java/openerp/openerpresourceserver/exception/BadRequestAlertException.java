package openerp.openerpresourceserver.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestAlertException extends RuntimeException {
    public BadRequestAlertException(String message) {
        super(message);
    }
}
