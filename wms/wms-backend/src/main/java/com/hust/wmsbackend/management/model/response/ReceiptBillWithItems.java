package com.hust.wmsbackend.management.model.response;

import com.hust.wmsbackend.management.entity.ReceiptBill;
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
public class ReceiptBillWithItems {
    private String receiptBillId;
    private BigDecimal totalPrice;
    private String description;
    private String receiptId;
    private String createdBy;
    private String createdStampStr;
    private String lastUpdateStampStr;
    private List<ProcessedItemModel> processedItems;

    public ReceiptBillWithItems(ReceiptBill bill) {
        this.receiptBillId = bill.getReceiptBillId();
        this.totalPrice = bill.getTotalPrice();
        this.description = bill.getDescription();
        this.receiptId = bill.getReceiptId().toString();
        this.createdBy = bill.getCreatedBy();
        this.createdStampStr = DateTimeFormat.convertDateToString(DateTimeFormat.DD_MM_YYYY_HH_MM_SS, bill.getCreatedStamp());
        this.lastUpdateStampStr = DateTimeFormat.convertDateToString(DateTimeFormat.DD_MM_YYYY_HH_MM_SS, bill.getLastUpdateStamp());
    }
}
