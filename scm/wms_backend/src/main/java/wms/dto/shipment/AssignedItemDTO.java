package wms.dto.shipment;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AssignedItemDTO {
    @NotBlank(message = "Shipment item code khong duoc de trong")
    private String shipmentItemCode;
    @NotBlank(message = "Trip code khong duoc de trong")
    private String tripCode;
}
