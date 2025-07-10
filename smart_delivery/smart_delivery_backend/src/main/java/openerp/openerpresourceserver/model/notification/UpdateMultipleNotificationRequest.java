package openerp.openerpresourceserver.model.notification;

import lombok.Getter;
import lombok.Setter;
import lombok.Value;

import jakarta.validation.constraints.NotBlank;
import java.util.Date;

@Getter
@Setter
@Value
public class UpdateMultipleNotificationRequest {
    @NotBlank
    String status;
    Date beforeOrAt;
}
