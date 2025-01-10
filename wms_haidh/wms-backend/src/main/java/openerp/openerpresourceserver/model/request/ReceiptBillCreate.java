package openerp.openerpresourceserver.model.request;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceiptBillCreate {
    private String receiptBillId;
    private String description;
    private String createdBy;
    private UUID receiptItemRequestId;
}

