package openerp.openerpresourceserver.thesisdefensejuryassignment.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "defense_jury_session") // Entity map voi bang defense_jury
public class DefenseJurySession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private int id;

    @ManyToOne
    @JoinColumn(name = "defense_session_id")
    private DefenseSession defenseSession;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "defense_jury_id")
    @JsonIgnore
    private DefenseJury defenseJury;

    public DefenseJurySession(DefenseSession defenseSession, DefenseJury defenseJury) {
        this.defenseSession = defenseSession;
        this.defenseJury = defenseJury;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public DefenseSession getDefenseSession() {
        return defenseSession;
    }

    public void setDefenseSession(DefenseSession defenseSession) {
        this.defenseSession = defenseSession;
    }

    public DefenseJury getDefenseJury() {
        return defenseJury;
    }

    public void setDefenseJury(DefenseJury defenseJury) {
        this.defenseJury = defenseJury;
    }

}
