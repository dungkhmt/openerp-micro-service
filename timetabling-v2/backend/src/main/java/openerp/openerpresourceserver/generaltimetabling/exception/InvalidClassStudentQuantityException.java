package openerp.openerpresourceserver.generaltimetabling.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InvalidClassStudentQuantityException extends RuntimeException{
    private String customMessage;
}
