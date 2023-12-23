package openerp.openerpresourceserver.exception;

import lombok.Getter;

@Getter
public class UnableSeparateClassException extends RuntimeException {

    private final String customMessage;

    public UnableSeparateClassException(String customMessage) {
        super();
        this.customMessage = customMessage;
    }
}


