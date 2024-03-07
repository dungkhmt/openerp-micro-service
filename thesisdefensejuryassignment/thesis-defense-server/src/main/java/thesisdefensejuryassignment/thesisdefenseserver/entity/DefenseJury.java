package thesisdefensejuryassignment.thesisdefenseserver.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "defense_jury") // Entity map voi bang defense_jury
public class DefenseJury {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "defense_date")
    private Date defenseDate;
    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "thesis_defense_plan_id", nullable = false, referencedColumnName = "id")
    @JsonBackReference
    private ThesisDefensePlan thesisDefensePlan;

    @CreatedDate
    @Column(name = "created_stamp")
    private Date createdTime;
    @Column(name = "max_nbr_thesis")
    private int maxThesis;

    @Column(name = "last_updated_stamp")
    private LocalDateTime updatedDateTime;

    public DefenseJury (Date defenseDate, String name, ThesisDefensePlan thesisDefensePlan, Date createdTime, int maxThesis,LocalDateTime updatedDateTime){
        this.defenseDate = defenseDate;
        this.name = name;
        this.thesisDefensePlan = thesisDefensePlan;
        this.createdTime = createdTime;
        this.updatedDateTime = updatedDateTime;
        this.maxThesis = maxThesis;
    }

    @ManyToMany
    @JoinTable(
            name ="defense_jury_keyword",
            joinColumns = @JoinColumn(name = "defense_jury_id"),
            inverseJoinColumns = @JoinColumn(name="keyword_id")
    )
    List<AcademicKeyword> academicKeywordList;

    public Date getDefenseDate() {
        return defenseDate;
    }

    public void setDefenseDate(Date defenseDate) {
        this.defenseDate = defenseDate;
    }

    public ThesisDefensePlan getThesisDefensePlan() {
        return thesisDefensePlan;
    }

    public void setThesisDefensePlan(ThesisDefensePlan thesisDefensePlan) {
        this.thesisDefensePlan = thesisDefensePlan;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    public void setUpdatedDateTime(LocalDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    public List<AcademicKeyword> getAcademicKeywordList() {
        return academicKeywordList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMaxThesis() {
        return maxThesis;
    }

    public void setMaxThesis(int maxThesis) {
        this.maxThesis = maxThesis;
    }

    public void setAcademicKeywordList(List<AcademicKeyword> academicKeywordList) {
        this.academicKeywordList = academicKeywordList;
    }

    public String getThesisDefensePlanId() {
        return thesisDefensePlan.getId();
    }


}
