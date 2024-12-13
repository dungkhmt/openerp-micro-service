package openerp.openerpresourceserver.thesisdefensejuryassignment.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "defense_session") // Entity map voi bang defense_session
public class DefenseSession implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private int id;
    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @OneToMany(mappedBy = "defenseSession", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<DefenseJurySession> defenseJuryList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
