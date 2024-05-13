package openerp.openerpresourceserver.generaltimetabling.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class InvalidFileInputException extends RuntimeException {
    private String errorMessage;
}
