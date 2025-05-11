package openerp.openerpresourceserver.dto.request;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleOrderCreateRequest {
    private Double deliveryFee;
    private Double totalProductCost;
    private Double totalOrderCost;
    private UUID customerAddressId;
    private String customerName;
    private String customerPhoneNumber;
    private String description;
    private String paymentType;
    private String orderType;
    private List<SaleOrderItemDTO> items;
}

