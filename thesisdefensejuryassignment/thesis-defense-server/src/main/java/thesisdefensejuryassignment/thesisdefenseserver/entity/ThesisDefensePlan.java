package thesisdefensejuryassignment.thesisdefenseserver.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "thesis_defense_plan")
public class ThesisDefensePlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(updatable = false, nullable = false, name = "id")
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "semester")
    private String semester;

    @LastModifiedDate
    @Column(name = "last_updated_stamp")
    private Date lastModifiedDate;

    @CreatedDate
    @Column(name = "created_stamp")
    private LocalDateTime createdTime;

    @Column(name="start_date")
    private Date startDate;

    @Column(name="end_date")
    private Date endDate;
    @OneToMany(mappedBy = "thesisDefensePlan")
//    @JsonIgnore
    private List<DefenseJury> defenseJuries;

    @OneToMany(mappedBy = "thesisDefensePlan")
//    @JsonIgnore
    private List<Thesis> thesisList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public ThesisDefensePlan(String id){
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public List<DefenseJury> getDefenseJuries() {
        return defenseJuries;
    }

    public void setDefenseJuries(List<DefenseJury> defenseJuries) {
        this.defenseJuries = defenseJuries;
    }

    public List<Thesis> getThesisList() {
        return thesisList;
    }

    public void setThesisList(List<Thesis> thesisList) {
        this.thesisList = thesisList;
    }
}
