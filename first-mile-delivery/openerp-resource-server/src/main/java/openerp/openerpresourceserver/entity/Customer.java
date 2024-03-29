package openerp.openerpresourceserver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fmd_customer")
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "collector_id")
    private UUID customerId;

    private String userId;

    private String customerName;
    private String address;
    private String phone;
    private String email;
    private String latitude;
    private String longitude;
    private String status;
    private String note;
}
