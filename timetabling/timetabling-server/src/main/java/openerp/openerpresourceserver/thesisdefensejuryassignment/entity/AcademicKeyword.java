package openerp.openerpresourceserver.thesisdefensejuryassignment.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "academic_keyword") // Entity map voi bang academic_keyword
@NoArgsConstructor
@AllArgsConstructor
public class AcademicKeyword implements Serializable {
    @Id
    @Column(name = "keyword")
    private String keyword;

    @Column(name = "description")
    private String description;

    @Column(name = "last_updated_stamp")
    private LocalDateTime updatedDateTime;

    @CreationTimestamp
    @Column(name = "created_stamp")
    private LocalDateTime createdTime;


    @ManyToMany(mappedBy = "academicKeywordList")
    @JsonIgnore
    private List<Teacher> teacherList;

    @ManyToMany(mappedBy = "academicKeywordList")
    @JsonIgnore
    private List<Thesis> thesisList;

    @ManyToMany(mappedBy = "academicKeywordList")
    @JsonIgnore
    private List<JuryTopic> juryTopicList;


    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    public void setUpdatedDateTime(LocalDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }
}