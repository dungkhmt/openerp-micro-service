package openerp.openerpresourceserver.generaltimetabling.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class NotFoundException extends RuntimeException{
    private String customMessage;
}
