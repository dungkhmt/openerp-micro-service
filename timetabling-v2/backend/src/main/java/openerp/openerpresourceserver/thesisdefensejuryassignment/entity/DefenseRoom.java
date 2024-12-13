package openerp.openerpresourceserver.thesisdefensejuryassignment.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "defense_room") // Entity map voi bang defense_jury
public class DefenseRoom implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private int id;
    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @OneToMany(mappedBy = "defenseRoom")
    @JsonIgnore
    private List<DefenseJury> defenseJuryList;
}
