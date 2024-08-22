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
@Table(name = "submission_hourly_summary")
public class SubmissionHourlySummary implements Serializable {
    @Id
    private String id;

    private String userSubmissionId;

    private Date submissionDate;

    private int hourOfDay;

    private int submissionCount;

    private int submissionPassCount;
}
