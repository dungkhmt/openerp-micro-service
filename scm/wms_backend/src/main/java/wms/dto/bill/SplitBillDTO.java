package wms.dto.bill;

import lombok.Data;
import wms.dto.purchase_order.PurchaseOrderItemDTO;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@Data
public class SplitBillDTO {
    @NotBlank(message = "Delivery bill code khong duoc de trong")
    private String deliveryBillCode;
    @Valid
    private List<SplitBillItemDTO> billItemDTOS;
}
