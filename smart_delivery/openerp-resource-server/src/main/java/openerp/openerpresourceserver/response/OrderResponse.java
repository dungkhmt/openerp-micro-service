package openerp.openerpresourceserver.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import openerp.openerpresourceserver.entity.*;
import openerp.openerpresourceserver.entity.enumentity.OrderStatus;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
public class OrderResponse {
    private UUID id;

    private Sender sender;

    private Recipient recipient;

    private Collector collector;

    private Shipper shipper;

    private String orderType;

    private OrderStatus status;

    private BigDecimal totalPrice;

    private BigDecimal shipPrice;

    private BigDecimal finalPrice;

    private Date expectedDeliveryDate;


    private String createdBy;
    private String approvedBy;
    private String cancelledBy;


    private Timestamp createdAt;

}
