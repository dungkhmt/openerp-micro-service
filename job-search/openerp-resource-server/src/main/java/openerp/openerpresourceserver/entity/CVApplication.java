package openerp.openerpresourceserver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "job_search_cv_application")
public class CVApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    private Integer id;
    private String status;
    @CreationTimestamp
    private Timestamp createdTime;
    @ManyToOne
    @JoinColumn(name = "employee_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "job_id")
    private JobPost jobId;
    private  Integer cvId;
}
