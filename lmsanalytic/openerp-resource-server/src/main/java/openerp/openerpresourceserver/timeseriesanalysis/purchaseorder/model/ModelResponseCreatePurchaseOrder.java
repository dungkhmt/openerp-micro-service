package openerp.openerpresourceserver.timeseriesanalysis.purchaseorder.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModelResponseCreatePurchaseOrder {
    PurchaseOrder po;
    List<SalesHistoryItem> predict;

}
