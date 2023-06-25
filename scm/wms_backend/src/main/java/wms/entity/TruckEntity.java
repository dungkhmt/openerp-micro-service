package wms.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "scm_truck")
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TruckEntity extends BaseEntity implements Serializable {
    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "size")
    private String size;

    @Column(name = "capacity")
    private double capacity;

    @Column(name = "speed")
    private double speed;

    @Column(name = "transport_cost_per_unit")
    private double transportCostPerUnit;

    @Column(name = "waiting_cost")
    private double waitingCost;

    // TODO: Check why we can't get userInfo in api get all drones
    @OneToOne(cascade = {CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH}, fetch = FetchType.EAGER)
//    @JsonIgnore
    @JsonInclude
    @JsonBackReference
    @JoinColumn(name = "user_id", referencedColumnName = "user_login_id")
    private UserRegister userLogin;
}
