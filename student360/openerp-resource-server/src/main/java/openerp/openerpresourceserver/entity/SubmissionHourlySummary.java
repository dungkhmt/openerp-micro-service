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

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "submission_hourly_summary")
public class SubmissionHourlySummary {
    @Id
    @Column(name = "submission_hourly_summary_id", updatable = false, nullable = false)
    private String id;

    private String userSubmissionId;

    private Date submissionDate;

    private int hourOfDay;

    private int submissionCount;

    private int submissionPassCount;

    @CreatedDate
    @Column(name = "created_stamp")
    private Date createdDate;

    @LastModifiedDate
    @Column(name = "last_updated_stamp")
    private Date lastModifiedDate;
}
