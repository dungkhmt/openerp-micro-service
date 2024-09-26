package wms.dto.shipment;

import lombok.Data;
import wms.entity.DeliveryBill;
import wms.entity.DeliveryTrip;
import wms.entity.Shipment;

import java.time.ZonedDateTime;

@Data
public class ReturnShipmentItemDTO {
    private Long id;
    private String code;
    private Shipment shipment;
    private DeliveryBill deliveryBill;
    private DeliveryTrip deliveryTrip;
    private Integer quantity;
    private String tripSeqId;
    private String productName;
    private ZonedDateTime createdDate;
    private int isDeleted;
}
