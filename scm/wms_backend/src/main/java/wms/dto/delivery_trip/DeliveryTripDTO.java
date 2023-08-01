package wms.dto.delivery_trip;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class DeliveryTripDTO {
    @NotBlank(message = "Shipment code khong duoc de trong")
    private String shipmentCode;
    private String userInCharge;
    private String startedDate;
    @NotBlank(message = "Facility code khong duoc de trong")
    private String facilityCode;
}
