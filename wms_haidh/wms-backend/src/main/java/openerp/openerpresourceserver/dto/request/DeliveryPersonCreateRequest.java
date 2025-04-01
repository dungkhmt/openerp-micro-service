package openerp.openerpresourceserver.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryPersonCreateRequest {
    private String email;
    private String fullName;
    private String phoneNumber;
}

