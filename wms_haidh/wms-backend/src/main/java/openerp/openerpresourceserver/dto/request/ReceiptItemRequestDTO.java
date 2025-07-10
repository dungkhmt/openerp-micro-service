package openerp.openerpresourceserver.dto.request;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptItemRequestDTO {
    
    private UUID productId;
    private Integer quantity;
    private UUID warehouseId;
}

