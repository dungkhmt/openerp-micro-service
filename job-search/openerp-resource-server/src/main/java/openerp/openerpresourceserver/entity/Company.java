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
@Table(name = "job_search_company")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private String about;
    private String companyType;
    private String companyLogoLink;
    private String companySize;
    private String country;
    private String workingDays;
    private String overtimePolicy;
    private String companyLocation;
    private String companyName;
}
