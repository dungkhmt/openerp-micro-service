package wms.dto.bill;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@Data
public class SplitBillDTO {
    @NotBlank(message = "Delivery bill code khong duoc de trong")
    private String deliveryBillCode;
    @NotBlank(message = "Delivery bill item khong duoc de trong")
    private String deliveryBillItemSeqId;
    @Positive(message = "So luong phai lon hon khong")
    private Integer quantity;
}
