package openerp.openerpresourceserver.generaltimetabling.exception;

import lombok.Getter;

@Getter
public class CourseUsedException extends RuntimeException {

    private final String customMessage;

    public CourseUsedException(String customMessage) {
        super();
        this.customMessage = customMessage;
    }}

