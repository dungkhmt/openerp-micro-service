package wms.dto.shipment;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
public class ShipmentItemDTO {
    @NotBlank(message = "Delivery bill code khong duoc de trong")
    private String deliveryBillCode;
    @NotBlank(message = "Delivery bill item khong duoc de trong")
    private String deliveryBillItemSeqId;
    @Positive(message = "So luong phai lon hon khong")
    private Integer quantity;
}
