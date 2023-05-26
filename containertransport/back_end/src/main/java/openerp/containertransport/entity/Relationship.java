package openerp.containertransport.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "container_transport_relationship")
public class Relationship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "from_facility")
    private Long fromFacility;

    @Column(name = "to_facility")
    private Long toFacility;

    @Column(name = "distant")
    private BigDecimal distant;

    @Column(name = "time")
    private Long time;
}
