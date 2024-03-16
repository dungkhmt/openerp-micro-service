package openerp.openerpresourceserver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "fmd_hub")
@EntityListeners(AuditingEntityListener.class)
public class Hub {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "hub_id")
    private UUID id;

    private String hubCode;

    private String hubName;

    private String address;


    private String createdByUserId;

    private String status;

    private String latitude;

    private String longitude;

    @CreatedDate
    @Column(name = "created_stamp")
    private Date createdDate;

    @LastModifiedDate
    @Column(name = "last_updated_stamp")
    private Date lastModifiedDate;

}
