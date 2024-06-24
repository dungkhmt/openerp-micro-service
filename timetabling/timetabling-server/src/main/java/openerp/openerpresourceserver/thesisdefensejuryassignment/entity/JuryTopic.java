package openerp.openerpresourceserver.thesisdefensejuryassignment.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "jury_topic") // Entity map voi bang defense_jury
public class JuryTopic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private int id;

    @Column(name = "name")
    private String name;

    @ManyToMany
    @JoinTable(
            name ="jury_topic_keyword",
            joinColumns = @JoinColumn(name = "jury_topic_id"),
            inverseJoinColumns = @JoinColumn(name="keyword_id")
    )
    private List<AcademicKeyword> academicKeywordList;

//    @OneToMany(mappedBy = "juryTopic")
//    @JsonIgnore
//    private List<DefenseJury> defenseJuryList;


    public JuryTopic(String name, List<AcademicKeyword> academicKeywordList) {
        this.name = name;
        this.academicKeywordList = academicKeywordList;
    }

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

    public List<AcademicKeyword> getAcademicKeywordList() {
        return academicKeywordList;
    }

    public void setAcademicKeywordList(List<AcademicKeyword> academicKeywordList) {
        this.academicKeywordList = academicKeywordList;
    }

//    public List<DefenseJury> getDefenseJury() {
//        return defenseJuryList;
//    }
//
//    public void setDefenseJury(List<DefenseJury> defenseJury) {
//        this.defenseJuryList = defenseJury;
//    }
}
