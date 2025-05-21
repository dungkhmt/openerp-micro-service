package openerp.openerpresourceserver.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "wms_sale_order_header")
public class Order {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID orderId;

	private String userLoginId;

	private LocalDateTime orderDate;

	private double deliveryFee;

	private double totalProductCost; // in VND

	private double totalOrderCost; // in VND

	private UUID customerAddressId;

	private String customerName;

	private String customerPhoneNumber;

	private String description;

	private String paymentType;

	private String orderType;

	private LocalDateTime lastUpdatedStamp;

	private LocalDateTime createdStamp;

	private String status;

	private String approvedBy;

	private String cancelledBy;

}
