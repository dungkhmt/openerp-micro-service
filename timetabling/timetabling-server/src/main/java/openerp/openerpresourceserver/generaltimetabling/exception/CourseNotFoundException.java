package openerp.openerpresourceserver.generaltimetabling.exception;

import lombok.Getter;

@Getter
public class CourseNotFoundException extends RuntimeException {

    private final String customMessage;

    public CourseNotFoundException(String customMessage) {
        super();
        this.customMessage = customMessage;
    }}

