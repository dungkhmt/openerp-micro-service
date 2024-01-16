package openerp.openerpresourceserver.exception;

import lombok.Getter;

@Getter
public class GroupUsedException extends RuntimeException {

    private final String customMessage;

    public GroupUsedException(String customMessage) {
        super();
        this.customMessage = customMessage;
    }
}

