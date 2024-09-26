package openerp.notification.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

import java.util.Date;

@Getter
@Setter
@Value
public class UpdateMultipleNotificationStatus {

    @NotBlank
    String status;

    Date beforeOrAt;
}
