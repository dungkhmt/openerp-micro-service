package openerp.openerpresourceserver.dto.request;

import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceiptItemCreateRequest {
    private int quantity;
    private UUID bayId;
    private String lotId;
    private double importPrice;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiredDate;
    private UUID receiptItemRequestId;
}

