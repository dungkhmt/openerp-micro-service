package openerp.openerpresourceserver.thesisdefensejuryassignment.entity.embedded;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.DefenseJury;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.Role;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.Teacher;

import java.io.Serializable;

@Entity
@Table(name = "defense_jury_teacher")
@AllArgsConstructor
@NoArgsConstructor
public class DefenseJuryTeacherRole implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    private int id;

    @ManyToOne
    @JoinColumn(name = "jury_id")
    @JsonIgnore
    private DefenseJury defenseJury;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public DefenseJury getDefenseJury() {
        return defenseJury;
    }

    public void setDefenseJury(DefenseJury defenseJury) {
        this.defenseJury = defenseJury;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public DefenseJuryTeacherRole(Teacher teacher, Role role, DefenseJury defenseJury) {
        this.teacher = teacher;
        this.role = role;
        this.defenseJury = defenseJury;
    }
}
