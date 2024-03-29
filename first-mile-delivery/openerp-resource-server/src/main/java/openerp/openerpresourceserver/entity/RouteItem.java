package openerp.openerpresourceserver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fmd_route_item")
@Builder
public class RouteItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    private UUID routeId;
    private UUID orderId;

    // số thứ tự đơn hàng trên route
    private int sequence;
}

