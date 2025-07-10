package openerp.openerpresourceserver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import openerp.openerpresourceserver.model.SubmissionScoreHistory;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "contest_submission_new")

public class ContestSubmission implements Serializable {

    @Id
    @Column(name = "contest_submission_id", updatable = false, nullable = false)
    private String id;

    private String contestId;

    private String problemId;

    private String userSubmissionId;

    private long point;

    private String status;

    private String sourceCodeLanguage;

    @CreatedDate
    @Column(name = "created_stamp")
    private Date createdDate;

    @LastModifiedDate
    @Column(name = "last_updated_stamp")
    private Date lastModifiedDate;
}
