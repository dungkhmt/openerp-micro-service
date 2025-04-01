package openerp.openerpresourceserver.generaltimetabling.exception;

import lombok.Getter;

@Getter
public class UnableSeparateClassException extends RuntimeException {

    private final String customMessage;

    public UnableSeparateClassException(String customMessage) {
        super();
        this.customMessage = customMessage;
    }
}


