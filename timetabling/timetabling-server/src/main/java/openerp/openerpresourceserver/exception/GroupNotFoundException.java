package openerp.openerpresourceserver.exception;

import lombok.Getter;

@Getter
public class GroupNotFoundException extends RuntimeException {

    private final String customMessage;

    public GroupNotFoundException(String customMessage) {
        super();
        this.customMessage = customMessage;
    }}

