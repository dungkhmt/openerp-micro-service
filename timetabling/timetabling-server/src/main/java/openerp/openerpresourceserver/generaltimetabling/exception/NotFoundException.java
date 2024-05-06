package openerp.openerpresourceserver.generaltimetabling.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotFoundException extends RuntimeException{
    private String customMessage;
    public NotFoundException(String customMessage) {
        super();
        this.customMessage = customMessage;
        System.err.println(customMessage);
    }
}
