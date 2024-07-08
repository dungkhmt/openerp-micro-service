package openerp.openerpresourceserver.thesisdefensejuryassignment.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "thesis") // Entity map voi bang defense_jury

public class Thesis implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "thesis_name")
    private String thesisName;

    @Column(name = "thesis_abstract")
    private String thesisAbstract;

    @Column(name = "student_name")
    private String studentName;

    @ManyToOne
    @JoinColumn(name = "supervisor_id", nullable = false)
    private Teacher supervisor;

    @ManyToOne
    @JoinColumn(name = "scheduled_jury_id", referencedColumnName = "id")
    @JsonBackReference
    private DefenseJury defenseJury;

    @Column(name = "last_updated_stamp")
    private LocalDateTime updatedDateTime;

    @CreationTimestamp
    @Column(name = "created_stamp")
    private Date createdTime;

    @ManyToMany
    @JoinTable(
            name ="thesis_keyword",
            joinColumns = @JoinColumn(name = "thesis_id"),
            inverseJoinColumns = @JoinColumn(name="keyword_id")
    )
    private List<AcademicKeyword> academicKeywordList;


    @ManyToOne
    @JoinColumn(name = "thesis_defense_plan_id", nullable = false, referencedColumnName = "id")
    @JsonBackReference
    private ThesisDefensePlan thesisDefensePlan;

    public ThesisDefensePlan getThesisDefensePlan() {
        return thesisDefensePlan;
    }

    public void setThesisDefensePlan(ThesisDefensePlan thesisDefensePlan) {
        this.thesisDefensePlan = thesisDefensePlan;
    }

    @ManyToOne
    @JoinColumn(name = "program_id", nullable = false)
    private TrainingProgram trainingProgram;
    @Column(name = "student_id")
    private String studentId;

    @Column(name = "student_email")
    private String studentEmail;

    @ManyToOne
    @JoinColumn(name = "scheduled_reviewer_id")
    private Teacher scheduledReviewer;

    @ManyToOne
    @JoinColumn(name = "jury_topic_id")
    private JuryTopic juryTopic;

    @ManyToOne
    @JoinColumn(name = "second_jury_topic_id")
    private JuryTopic secondaryJuryTopic;


    /*-------------------------------------------------------*/
    public Teacher getScheduledReviewer() {
        return scheduledReviewer;
    }

    public void setScheduledReviewer(Teacher scheduledReviewer) {
        this.scheduledReviewer = scheduledReviewer;
    }

    public List<AcademicKeyword> getAcademicKeywordList() {
        return academicKeywordList;
    }

    public void setAcademicKeywordList(List<AcademicKeyword> academicKeywordList) {
        this.academicKeywordList = academicKeywordList;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getThesisName() {
        return thesisName;
    }

    public void setThesisName(String thesisName) {
        this.thesisName = thesisName;
    }

    public String getThesisAbstract() {
        return thesisAbstract;
    }

    public void setThesisAbstract(String thesisAbstract) {
        this.thesisAbstract = thesisAbstract;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public Teacher getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(Teacher supervisor) {
        this.supervisor = supervisor;
    }

    public DefenseJury getDefenseJury() {
        return defenseJury;
    }

    public void setDefenseJury(DefenseJury defenseJury) {
        this.defenseJury = defenseJury;
    }

    public LocalDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    public void setUpdatedDateTime(LocalDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Thesis(String thesisName, String thesisAbstract, String studentName, Teacher supervisor, Date createdTime, List<AcademicKeyword> academicKeywordList, ThesisDefensePlan defensePlan, TrainingProgram program, String studentId, String studentEmail) {
        this.thesisName = thesisName;
        this.thesisAbstract = thesisAbstract;
        this.studentName = studentName;
        this.supervisor = supervisor;
        this.createdTime = createdTime;
        this.academicKeywordList = academicKeywordList;
        this.thesisDefensePlan = defensePlan;
        this.trainingProgram = program;
        this.studentId = studentId;
        this.studentEmail = studentEmail;
    }

    public JuryTopic getJuryTopic() {
        return juryTopic;
    }

    public void setJuryTopic(JuryTopic juryTopic) {
        this.juryTopic = juryTopic;
    }

    public JuryTopic getSecondaryJuryTopic() {
        return secondaryJuryTopic;
    }

    public void setSecondaryJuryTopic(JuryTopic secondaryJuryTopic) {
        this.secondaryJuryTopic = secondaryJuryTopic;
    }
}
