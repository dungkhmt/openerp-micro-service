package openerp.openerpresourceserver.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    private UUID itemId;
    private UUID warehouseId;
    private UUID customerAddressId;
    private double weight;
}
