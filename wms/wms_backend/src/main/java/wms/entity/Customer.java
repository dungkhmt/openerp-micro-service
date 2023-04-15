package wms.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "customer")
public class Customer {
    @Id
    @Column(name = "code", nullable = false, length = 20)
    private String id;

    @Column(name = "id", nullable = false)
    private Integer id1;

    @Column(name = "name", length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_code")
    private CustomerType customerCode;

    @Column(name = "phone_number", length = 100)
    private String phoneNumber;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "status", length = 20)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_code")
    private Facility facilityCode;

    @Column(name = "latitude", length = 20)
    private String latitude;

    @Column(name = "longitude", length = 20)
    private String longitude;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private UserLogin createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_type_code")
    private ContractType contractTypeCode;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

}