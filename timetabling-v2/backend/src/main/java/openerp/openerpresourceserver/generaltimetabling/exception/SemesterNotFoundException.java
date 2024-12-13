package openerp.openerpresourceserver.generaltimetabling.exception;

import lombok.Getter;

@Getter
public class SemesterNotFoundException extends RuntimeException {

    private final String customMessage;

    public SemesterNotFoundException(String customMessage) {
        super();
        this.customMessage = customMessage;
    }}

