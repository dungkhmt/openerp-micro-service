package openerp.openerpresourceserver.model.request;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptItemRequestCreate {
    
    private UUID productId;
    private Integer quantity;
    private UUID warehouseId;
}
