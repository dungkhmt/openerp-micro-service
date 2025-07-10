package openerp.openerpresourceserver.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "problem_submission_new")

public class ProblemSubmission implements Serializable {
    @Id
    @Column(name = "problem_submission_id", updatable = false, nullable = false)
    private String id;

    private String problemId;

    private String submittedByUserLoginId;

    private String status;

    private int score;

    @Column(name = "created_stamp")
    private String createdDate;
}
