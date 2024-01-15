package openerp.openerpresourceserver.exception;

import lombok.Getter;

@Getter
public class NotFoundGroupException extends RuntimeException {

    private final String customMessage;

    public NotFoundGroupException(String customMessage) {
        super();
        this.customMessage = customMessage;
    }
}

