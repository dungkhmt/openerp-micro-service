package openerp.openerpresourceserver.timeseriesanalysis.purchaseorder.model;

import jakarta.persistence.criteria.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SalesHistoryItem {
    private String date;
    List<OrderItem> orderItems;
}
