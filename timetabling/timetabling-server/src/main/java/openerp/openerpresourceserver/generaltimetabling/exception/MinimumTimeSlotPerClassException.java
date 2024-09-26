package openerp.openerpresourceserver.generaltimetabling.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MinimumTimeSlotPerClassException extends RuntimeException {
    private String errorMessage;
}
