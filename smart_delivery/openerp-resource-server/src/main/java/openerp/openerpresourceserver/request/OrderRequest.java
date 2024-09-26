package openerp.openerpresourceserver.request;

import lombok.Data;
import openerp.openerpresourceserver.entity.OrderItem;
import openerp.openerpresourceserver.entity.Sender;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderRequest {
    private String senderName;

    private String senderPhone;

    private String senderEmail;

    private String senderAddress;

    private BigDecimal senderLongitude;

    private BigDecimal senderLatitude;

    private String recipientName;

    private String recipientPhone;

    private String recipientEmail;

    private String recipientAddress;

    private BigDecimal recipientLongitude;

    private BigDecimal recipientLatitude;

    private List<OrderItem> items;

    private BigDecimal totalprice;

    private String orderType;


}
