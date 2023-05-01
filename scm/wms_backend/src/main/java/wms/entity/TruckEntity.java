package wms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "scm_truck")
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TruckEntity extends BaseEntity {
    @Column(name = "code")
    private String code;

    @Column(name = "capacity")
    private double capacity;

    @Column(name = "speed")
    private double speed;

    @Column(name = "transport_cost_per_unit")
    private double transportCostPerUnit;

    @Column(name = "waiting_cost")
    private double waitingCost;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JsonIgnore
    @JoinColumn(name = "user_id", referencedColumnName = "user_login_id")
    private UserLogin userLogin;
}
