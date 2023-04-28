package wms.dto.bill;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
public class SplitBillItemDTO {
    @NotBlank(message = "Delivery bill item khong duoc de trong")
    private String deliveryBillItemSeqId;
    @NotBlank(message = "Product code khong duoc de trong")
    private String productCode;
    @Positive(message = "So luong phai lon hon khong")
    private Integer quantity;
}
