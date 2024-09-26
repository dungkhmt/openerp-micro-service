package wms.dto.shipment;

import wms.entity.DeliveryBill;
import wms.entity.DeliveryTrip;
import wms.entity.Shipment;


public interface ShipmentProjection {
    String getCode();
    Integer getQuantity();
    String getDeliveryBillItemSeqId();

    String getTripSeqId();
    Shipment getShipment();

    DeliveryBill getDeliveryBill();

    DeliveryTrip getDeliveryTrip();


}
