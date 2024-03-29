package openerp.openerpresourceserver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fmd_order")
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "order_id")
    private UUID id;

    private UUID customerId;

    private Double weight;

    private Double volume;

    private Date fromDateTime;

    private Date toDateTime;

    private String address;

    private String latitude;

    private String longitude;


    /**
     * Trạng thái của đơn hàng.
     * <ul>
     *   <li><strong>PENDING:</strong> Đơn hàng chờ xử lý.</li>
     *   <li><strong>IN_PROGRESS:</strong> Đơn hàng đang trong quá trình xử lý.</li>
     *   <li><strong>COMPLETED:</strong> Đơn hàng đã hoàn thành.</li>
     *   <li><strong>CANCELLED:</strong> Đơn hàng đã bị hủy bỏ.</li>
     * </ul>
     */
    private String status;

    @CreatedDate
    @Column(name = "created_stamp")
    private Date createdDate;

    @LastModifiedDate
    @Column(name = "last_updated_stamp")
    private Date lastModifiedDate;

    public static final String PENDING = "PENDING";
    public static final String IN_PROGRESS = "IN_PROGRESS";
    public static final String COMPLETED = "COMPLETED";
    public static final String CANCELLED = "CANCELLED";
}
