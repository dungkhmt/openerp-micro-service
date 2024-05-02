package openerp.openerpresourceserver.generaltimetabling.exception;

import lombok.Getter;

@Getter
public class ClassroomNotFoundException extends RuntimeException {

    private final String customMessage;

    public ClassroomNotFoundException(String customMessage) {
        super();
        this.customMessage = customMessage;
    }
}

