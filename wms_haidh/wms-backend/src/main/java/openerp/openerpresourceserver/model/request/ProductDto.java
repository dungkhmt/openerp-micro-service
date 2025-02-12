package openerp.openerpresourceserver.model.request;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {
    private UUID productId;
    private String code;
    private String name;
    private String description;
    private BigDecimal height;
    private BigDecimal weight;
    private BigDecimal area;
    private String uom;
    private UUID categoryId;
    private String imageContentType;
    private Long imageSize;
    private byte[] imageData;
}

