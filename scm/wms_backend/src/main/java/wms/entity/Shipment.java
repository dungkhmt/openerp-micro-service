package wms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@Table(name = "scm_shipment")
@NoArgsConstructor
@AllArgsConstructor
public class Shipment extends BaseEntity implements Serializable {
    @Column(name = "code")
    private String code;

    @Column(name = "title")
    private String title;

    @Column(name = "started_date")
    private ZonedDateTime startedDate;

    @Column(name = "expected_end_date")
    private ZonedDateTime endedDate;

    @Column(name = "max_size_item")
    private Integer maxSize;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by", referencedColumnName = "user_login_id")
    @NotFound(action = NotFoundAction.IGNORE)
    private UserRegister user;

    @OneToMany(mappedBy = "shipment",fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<DeliveryTrip> trips;

    @OneToMany(mappedBy = "shipment",fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<ShipmentItem> shipmentItems;
}
