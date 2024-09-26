package openerp.openerpresourceserver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.sql.Timestamp;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "job_search_working_experience")

public class Experience {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    private Integer id;
    private String  workingPosition;
    private String companyName;
    private String responsibility;
    private Timestamp startingTime;
    private Timestamp endingTime;
    @CreationTimestamp
    @Column(name = "created_time")
    private Date createdTime;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
