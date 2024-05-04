package openerp.openerpresourceserver.entity;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
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
@Table(name = "job_search_employee_cv")
public class EmployeeCV {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    private Integer id;
    private String title;
    private String description;
    private String linkedInLink;
    private String githubLink;
    private String userName;
    private String gender;
    private String cvLink;
    private String profession;
    private String mobilePhone;
    private String email;
    private String userLocation;
    @CreationTimestamp
    @Column(name = "created_time")
    private Timestamp createdTime;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}