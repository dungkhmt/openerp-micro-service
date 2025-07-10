package openerp.openerpresourceserver.generaltimetabling.exception;

import lombok.Getter;

@Getter
public class ConflictScheduleException extends RuntimeException {

    private final String customMessage;

    public ConflictScheduleException(String customMessage) {
        super();
        this.customMessage = customMessage;
        System.err.println(customMessage);
    }
}

