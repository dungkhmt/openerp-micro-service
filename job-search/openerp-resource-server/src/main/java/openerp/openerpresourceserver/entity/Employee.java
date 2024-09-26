package openerp.openerpresourceserver.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "job_search_employee")
public class Employee {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    private String user_login_id;

    private String email;

    private String name;

    private String avatar_link;

    private String gender;

    private String date_of_birth;

    private String location;

    private String mobile_phone;

    private String linkedin_link;

    private String github_link;

    @CreationTimestamp
    @Column(name = "created_time")
    private Date createdDate;


}
