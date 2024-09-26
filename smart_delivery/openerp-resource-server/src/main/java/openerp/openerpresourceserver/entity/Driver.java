package openerp.openerpresourceserver.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import openerp.openerpresourceserver.entity.enumentity.EmploymentStatus;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.security.Timestamp;
import java.util.Date;
import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "smartdelivery_driver")
public class Driver {
    @Id
    @GenericGenerator(name = "uuid1", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue(generator = "uuid1")
    private UUID driverId;

    private String name;

    private String username;

    private String password;

    private String email;

    private String phone;

    private String address;

    private String licenseNumber;

    private Enum<EmploymentStatus> employment_status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Hub originHub;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Hub finalHub;

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;

}
