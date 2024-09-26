package com.hust.wmsbackend.management.model.response;

import com.hust.wmsbackend.management.entity.DeliveryBill;
import com.hust.wmsbackend.management.model.DeliveryTripDTO;
import com.hust.wmsbackend.management.utils.DateTimeFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Builder
@NoArgsConstructor
@Data
@AllArgsConstructor
public class DeliveryBillWithItems {
    private String deliveryBillId;
    private BigDecimal totalPrice;
    private String description;
    private String deliveryTripId;
    private String createdBy;
    private String createdStampStr;
    private String lastUpdateStampStr;
    private List<DeliveryTripDTO.DeliveryTripItemDTO> items;

    public DeliveryBillWithItems(DeliveryBill deliveryBill) {
        this.deliveryBillId = deliveryBill.getDeliveryBillId();
        this.totalPrice = deliveryBill.getTotalPrice();
        this.description = deliveryBill.getDescription();
        this.deliveryTripId = deliveryBill.getDeliveryTripId();
        this.createdBy = deliveryBill.getCreatedBy();
        this.createdStampStr = DateTimeFormat.convertDateToString(DateTimeFormat.DD_MM_YYYY, deliveryBill.getCreatedStamp());
        this.lastUpdateStampStr = DateTimeFormat.convertDateToString(DateTimeFormat.DD_MM_YYYY, deliveryBill.getLastUpdateStamp());
    }
}
