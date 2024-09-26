package wms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "scm_user_register")
public class UserRegister implements Serializable {
    @Id
    @Size(max = 255)
    @Column(name = "user_login_id", nullable = false)
    private String id;

    @Size(max = 100)
    @Column(name = "password", length = 100)
    private String password;

    @Size(max = 100)
    @Column(name = "email", length = 100)
    private String email;

    @Size(max = 100)
    @Column(name = "first_name", length = 100)
    private String firstName;

    @Size(max = 100)
    @Column(name = "middle_name", length = 100)
    private String middleName;

    @Size(max = 100)
    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(name = "status_id", length = 60)
    private String status_id;

    @Column(name = "registered_roles")
    private String registeredRoles;

    @Column(name = "last_updated_stamp")
    private Instant lastUpdatedStamp;

    @Column(name = "created_stamp")
    private Instant createdStamp;

    @Column(name = "affiliations", length = 200)
    private String affiliations;

    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY)
    // Add JsonIgnore: https://stackoverflow.com/questions/20813496/tomcat-exception-cannot-call-senderror-after-the-response-has-been-committed
    @JsonIgnore
    private Set<Customer> customers;

    @OneToMany(mappedBy = "creator",fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Facility> facilities;

    @OneToMany(mappedBy = "manager",fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Facility> listFacilities;

    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<PurchaseOrder> purchaseOrders;

    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Shipment> shipments;

    @OneToMany(mappedBy = "creator",fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<DeliveryTrip> trips;

    @OneToMany(mappedBy = "userInCharge",fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<DeliveryTrip> listTrips;

    @OneToOne(mappedBy = "userLogin", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private DroneEntity drone;

    @OneToOne(mappedBy = "userLogin", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    @JsonManagedReference
    private TruckEntity truck;

}
