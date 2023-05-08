package com.hust.wmsbackend.management.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReceiptGeneralResponse {
    private String receiptId;
    private String receiptName;
    private String warehouseName;
    private String receivedDate;
    private String createdDate;
}
