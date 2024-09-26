package openerp.openerpresourceserver.thesisdefensejuryassignment.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.embedded.DefenseJuryTeacherRole;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "teacher") // Entity map voi bang defense_jury
public class Teacher implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private String id;

    @Column(name = "teacher_name")
    private String teacherName;

    @Column(name = "user_login_id")
    private String userLoginId;

    @Column(name = "last_updated_stamp")
    private LocalDateTime lastUpdatedStamp;

    @CreatedDate
    @Column(name = "created_stamp")
    private LocalDateTime createdStamp;

    @Column(name = "max_credit")
    private int maxCredit;

    @ManyToMany
    @JoinTable(
            name ="teacher_keyword",
            joinColumns = @JoinColumn(name = "teacher_id"),
            inverseJoinColumns = @JoinColumn(name="keyword")
    )
    private List<AcademicKeyword> academicKeywordList;

    @OneToMany(mappedBy = "supervisor")
    @JsonIgnore
    private List<Thesis> thesisList;

    @OneToMany(mappedBy = "teacher")
    @JsonIgnore
    private List<DefenseJuryTeacherRole> defenseJuryRole;

    @OneToMany(mappedBy = "scheduledReviewer")
    @JsonIgnore
    private List<Thesis> scheduledReviewedThesisList;

    /*------------------------------------------------------------------*/
    public List<AcademicKeyword> getAcademicKeywordList() {
        return academicKeywordList;
    }

    public void setAcademicKeywordList(List<AcademicKeyword> academicKeywordList) {
        this.academicKeywordList = academicKeywordList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getUserLoginId() {
        return userLoginId;
    }

    public void setUserLoginId(String userLoginId) {
        this.userLoginId = userLoginId;
    }

    public LocalDateTime getLastUpdatedStamp() {
        return lastUpdatedStamp;
    }

    public void setLastUpdatedStamp(LocalDateTime lastUpdatedStamp) {
        this.lastUpdatedStamp = lastUpdatedStamp;
    }

    public LocalDateTime getCreatedStamp() {
        return createdStamp;
    }

    public void setCreatedStamp(LocalDateTime createdStamp) {
        this.createdStamp = createdStamp;
    }

    public int getMaxCredit() {
        return maxCredit;
    }

    public void setMaxCredit(int maxCredit) {
        this.maxCredit = maxCredit;
    }
}
