package wms.dto.shipment;

import lombok.Data;
import wms.entity.DeliveryBill;
import wms.entity.DeliveryTrip;
import wms.entity.Shipment;

@Data
public class ReturnShipmentItemDTO {
    private String code;
    private Shipment shipment;
    private DeliveryBill deliveryBill;
    private DeliveryTrip deliveryTrip;
    private Integer quantity;
    private String tripSeqId;
    private String productName;
}
