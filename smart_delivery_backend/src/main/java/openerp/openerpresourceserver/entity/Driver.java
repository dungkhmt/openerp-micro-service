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
public class Driver extends Employee{

    private String licenseNumber;
    @Enumerated(EnumType.STRING)
    private EmploymentStatus employment_status;

    private UUID originHubId;
    private String originHubName;

    private UUID finalHubId;
    private String finalHubName;

}
