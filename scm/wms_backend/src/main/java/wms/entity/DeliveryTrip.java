package wms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@Table(name = "scm_delivery_trip")
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryTrip extends BaseEntity implements Serializable {
    @Column(name = "code")
    private String code;

    @Column(name = "started_date")
    private ZonedDateTime startedDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by", referencedColumnName = "user_login_id")
    @NotFound(action = NotFoundAction.IGNORE)
    private UserRegister creator;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_in_charge", referencedColumnName = "user_login_id")
    @NotFound(action = NotFoundAction.IGNORE)
    private UserRegister userInCharge;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "shipment_code", referencedColumnName = "code")
    @NotFound(action = NotFoundAction.IGNORE)
    private Shipment shipment;

    @OneToMany(mappedBy = "deliveryTrip",fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<ShipmentItem> shipmentItems = new HashSet<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "facility_code", referencedColumnName = "code")
    @NotFound(action = NotFoundAction.IGNORE)
    private Facility facility;
}
