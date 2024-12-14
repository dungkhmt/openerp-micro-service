package openerp.openerpresourceserver.generaltimetabling.exception;

import lombok.Getter;

@Getter
public class ClassroomUsedException extends RuntimeException {

    private final String customMessage;

    public ClassroomUsedException(String customMessage) {
        super();
        this.customMessage = customMessage;
    }}

