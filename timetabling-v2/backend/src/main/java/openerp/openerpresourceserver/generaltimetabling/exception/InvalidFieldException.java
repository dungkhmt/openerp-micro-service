package openerp.openerpresourceserver.generaltimetabling.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class InvalidFieldException extends RuntimeException {
    private String errorMessage;
}
