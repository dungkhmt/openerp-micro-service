package openerp.openerpresourceserver.exception;

import lombok.Getter;

@Getter
public class ConflictScheduleException extends RuntimeException {

    private final String customMessage;

    public ConflictScheduleException(String customMessage) {
        super();
        this.customMessage = customMessage;
    }}

