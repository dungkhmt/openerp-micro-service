package wms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "scm_facility")
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
// https://stackoverflow.com/questions/4525186/cannot-be-cast-to-java-io-serializable
public class Facility extends BaseEntity implements Serializable {
    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

//    @Column(name = "phone_number")
//    private String phone;

    @Column(name = "address")
    private String address;

    @Column(name = "status")
    private String status;

    @Column(name = "latitude")
    private String latitude;

    @Column(name = "longitude")
    private String longitude;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by", referencedColumnName = "user_login_id")
    @NotFound(action = NotFoundAction.IGNORE)
    private UserRegister creator;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "managed_by", referencedColumnName = "user_login_id")
    @NotFound(action = NotFoundAction.IGNORE)
    private UserRegister manager;

    @OneToMany(mappedBy = "facility",fetch = FetchType.LAZY)
    // Add JsonIgnore: https://stackoverflow.com/questions/20813496/tomcat-exception-cannot-call-senderror-after-the-response-has-been-committed
    @JsonIgnore
    private Set<Customer> customers;
//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "contract_type_code")
//    @NotFound(action = NotFoundAction.IGNORE)
//    private ContractType contractType;

    @OneToMany(
            mappedBy = "facility",
            fetch = FetchType.LAZY
//            cascade = CascadeType.ALL
//            orphanRemoval = true
    )
    // https://stackoverflow.com/questions/3325387/infinite-recursion-with-jackson-json-and-hibernate-jpa-issue
//    @JsonManagedReference
    @JsonIgnore
    private List<ProductFacility> productFacilities;


    @OneToMany(mappedBy = "facility",fetch = FetchType.LAZY)
    // Add JsonIgnore: https://stackoverflow.com/questions/20813496/tomcat-exception-cannot-call-senderror-after-the-response-has-been-committed
    @JsonIgnore
    private Set<PurchaseOrder> purchaseOrders;

    @OneToMany(mappedBy = "facility",fetch = FetchType.LAZY)
    // Add JsonIgnore: https://stackoverflow.com/questions/20813496/tomcat-exception-cannot-call-senderror-after-the-response-has-been-committed
    @JsonIgnore
    private Set<InventoryItem> inventoryItems;

    @OneToMany(mappedBy = "facility",fetch = FetchType.LAZY)
    // Add JsonIgnore: https://stackoverflow.com/questions/20813496/tomcat-exception-cannot-call-senderror-after-the-response-has-been-committed
    @JsonIgnore
    private Set<ReceiptBill> receiptBills;

    @OneToMany(mappedBy = "facility",fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<DeliveryTrip> deliveryTrips;
}
