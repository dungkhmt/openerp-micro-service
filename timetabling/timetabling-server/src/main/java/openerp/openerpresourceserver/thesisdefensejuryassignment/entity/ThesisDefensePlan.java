package openerp.openerpresourceserver.thesisdefensejuryassignment.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Data

@NoArgsConstructor
@Table(name = "thesis_defense_plan")
public class ThesisDefensePlan implements Serializable {
    @Id
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
//    @OneToMany(mappedBy = "thesisDefensePlan")
////    @JsonIgnore
//    private List<DefenseJury> defenseJuries;
    @OneToMany(mappedBy = "thesisDefensePlan")
    @JsonIgnore
    private List<JuryTopic> planTopicList;

    @OneToMany(mappedBy = "thesisDefensePlan")
//    @JsonIgnore
    private List<Thesis> thesisList;

    public ThesisDefensePlan(String id, String name, String description, String semester, Date startDate, Date endDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.semester = semester;
        this.startDate = startDate;
        this.endDate = endDate;
    }

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

//    public List<DefenseJury> getDefenseJuries() {
//        return defenseJuries;
//    }
//
//    public void setDefenseJuries(List<DefenseJury> defenseJuries) {
//        this.defenseJuries = defenseJuries;
//    }

    public List<Thesis> getThesisList() {
        return thesisList;
    }

    public void setThesisList(List<Thesis> thesisList) {
        this.thesisList = thesisList;
    }

    public List<JuryTopic> getPlanTopicList() {
        return planTopicList;
    }

    public void setPlanTopicList(List<JuryTopic> planTopicList) {
        this.planTopicList = planTopicList;
    }

}
