package openerp.openerpresourceserver.dto.request;

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
public class ReceiptCreateRequest {

    private String receiptName;
    private String description;
    private UUID warehouseId;
    private UUID supplierId; 
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expectedReceiptDate;
    private List<ReceiptItemRequestDTO> receiptItemRequests;  
    
}



