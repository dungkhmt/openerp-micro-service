package openerp.openerpresourceserver.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryPersonRequest {
    private String email;
    private String fullName;
    private String phoneNumber;
}

