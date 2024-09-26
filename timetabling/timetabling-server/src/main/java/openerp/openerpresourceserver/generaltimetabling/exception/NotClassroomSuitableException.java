package openerp.openerpresourceserver.generaltimetabling.exception;

import lombok.Getter;

@Getter
public class NotClassroomSuitableException extends RuntimeException {

    private final String customMessage;

    public NotClassroomSuitableException(String customMessage) {
        super();
        this.customMessage = customMessage;
    }
}

