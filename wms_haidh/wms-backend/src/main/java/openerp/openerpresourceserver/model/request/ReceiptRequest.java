package openerp.openerpresourceserver.model.request;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptRequest {

    private String receiptName;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate receiptDate;
    private UUID warehouseId;
    private String createdReason;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expectedReceiptDate;
    private String createdBy;
    private List<ReceiptItemRequestCreate> receiptItemRequests;  
    
}



