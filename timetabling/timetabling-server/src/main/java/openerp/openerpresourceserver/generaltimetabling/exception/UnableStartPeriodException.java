package openerp.openerpresourceserver.generaltimetabling.exception;

import lombok.Getter;

@Getter
public class UnableStartPeriodException extends RuntimeException {

    private final String customMessage;

    public UnableStartPeriodException(String customMessage) {
        super();
        this.customMessage = customMessage;
    }
}


