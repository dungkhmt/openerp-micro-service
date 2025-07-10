package openerp.openerpresourceserver.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierCreateRequest {
    private String name;
    private String address;
    private String email;
    private String phone;
}
