package openerp.openerpresourceserver.dto.request;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCreateRequest {
    private UUID productId;
    private String code;
    private String name;
    private String description;
    private double height;
    private double weight;
    private String uom;
    private UUID categoryId;
}

