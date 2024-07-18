package openerp.openerpresourceserver.thesisdefensejuryassignment.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.embedded.DefenseJuryTeacherRole;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "defense_jury") // Entity map voi bang defense_jury
public class DefenseJury implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "defense_date")
    private Date defenseDate;
    @Column(name = "name", unique = true, nullable = false)
    private String name;

//    @ManyToOne
//    @JoinColumn(name = "thesis_defense_plan_id", nullable = false, referencedColumnName = "id")
//    @JsonBackReference
//    private ThesisDefensePlan thesisDefensePlan;

    @CreatedDate
    @Column(name = "created_stamp")
    private Date createdTime;
    @Column(name = "max_nbr_thesis")
    private int maxThesis;

    @Column(name = "last_updated_stamp")
    private LocalDateTime updatedDateTime;

    @ManyToOne
    @JoinColumn(name ="defense_room_id", nullable = false, referencedColumnName = "id")
    private DefenseRoom defenseRoom;

//    @ManyToOne
//    @JoinColumn(name ="defense_session_id", nullable = false, referencedColumnName = "id")
//    private DefenseSession defenseSession;

//    @ManyToMany
//    @JoinTable(
//            name ="defense_jury_keyword",
//            joinColumns = @JoinColumn(name = "defense_jury_id"),
//            inverseJoinColumns = @JoinColumn(name="keyword_id")
//    )
//    private List<AcademicKeyword> academicKeywordList;

    @OneToMany(mappedBy = "defenseJury")
    private List<Thesis> thesisList;
    @OneToMany(mappedBy = "defenseJury")
    private List<DefenseJuryTeacherRole> defenseJuryTeacherRoles;

//    @ManyToOne
//    @JoinColumn(name ="jury_topic_id", referencedColumnName = "id")
//    private JuryTopic juryTopic;

    @ManyToOne
    @JoinColumn(name ="defense_jury_topic_id", referencedColumnName = "id")
    @JsonIgnore
    private JuryTopic planTopic;

    @Column(name = "isassigned")
    private boolean isAssigned;
    @OneToMany(mappedBy = "defenseJury", cascade = CascadeType.ALL)
    private List<DefenseJurySession> defenseJurySessionList;
    /*----------------------------------------------------------------*/
    public DefenseJury (Date defenseDate, String name, ThesisDefensePlan thesisDefensePlan, Date createdTime, int maxThesis,LocalDateTime updatedDateTime){
        this.defenseDate = defenseDate;
        this.name = name;
//        this.thesisDefensePlan = thesisDefensePlan;
        this.createdTime = createdTime;
        this.updatedDateTime = updatedDateTime;
        this.maxThesis = maxThesis;
    }
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone = "Asia/Saigon")
    public Date getDefenseDate() {
        return defenseDate;
    }

    public void setDefenseDate(Date defenseDate) {
        this.defenseDate = defenseDate;
    }

//    public ThesisDefensePlan getThesisDefensePlan() {
//        return thesisDefensePlan;
//    }
//
//    public void setThesisDefensePlan(ThesisDefensePlan thesisDefensePlan) {
//        this.thesisDefensePlan = thesisDefensePlan;
//    }

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

//    public List<AcademicKeyword> getAcademicKeywordList() {
//        return academicKeywordList;
//    }

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

//    public void setAcademicKeywordList(List<AcademicKeyword> academicKeywordList) {
//        this.academicKeywordList = academicKeywordList;
//    }

//    public String getThesisDefensePlanId() {
//        return thesisDefensePlan.getId();
//    }

    public DefenseRoom getDefenseRoom() {
        return defenseRoom;
    }

    public void setDefenseRoom(DefenseRoom defenseRoom) {
        this.defenseRoom = defenseRoom;
    }

//    public DefenseSession getDefenseSession() {
//        return defenseSession;
//    }
//
//    public void setDefenseSession(DefenseSession defenseSession) {
//        this.defenseSession = defenseSession;
//    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public List<Thesis> getThesisList() {
        return thesisList;
    }

    public void setThesisList(List<Thesis> thesisList) {
        this.thesisList = thesisList;
    }

    public List<DefenseJuryTeacherRole> getDefenseJuryTeacherRoles() {
        return defenseJuryTeacherRoles;
    }

    public void setDefenseJuryTeacherRoles(List<DefenseJuryTeacherRole> defenseJuryTeacherRoles) {
        this.defenseJuryTeacherRoles = defenseJuryTeacherRoles;
    }

    public boolean isAssigned() {
        return isAssigned;
    }

    public void setAssigned(boolean assigned) {
        isAssigned = assigned;
    }

    public JuryTopic getPlanTopic() {
        return planTopic;
    }

    public void setPlanTopic(JuryTopic planTopic) {
        this.planTopic = planTopic;
    }

    public List<DefenseJurySession> getDefenseJurySessionList() {
        return defenseJurySessionList;
    }

    public void setDefenseJurySessionList(List<DefenseJurySession> defenseJurySessionList) {
        this.defenseJurySessionList = defenseJurySessionList;
    }
}
