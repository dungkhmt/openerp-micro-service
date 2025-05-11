package openerp.openerpresourceserver.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerAddressCreateRequest {
    private String addressName;
    private Double longitude;
    private Double latitude;
}

