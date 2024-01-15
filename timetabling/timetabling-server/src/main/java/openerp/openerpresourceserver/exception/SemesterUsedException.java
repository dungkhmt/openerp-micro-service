package openerp.openerpresourceserver.exception;

import lombok.Getter;

@Getter
public class SemesterUsedException extends RuntimeException {

    private final String customMessage;

    public SemesterUsedException(String customMessage) {
        super();
        this.customMessage = customMessage;
    }}

