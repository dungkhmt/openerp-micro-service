package openerp.openerpresourceserver.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.security.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "smartdelivery_collector")
public class Collector {
    @Id
    @GenericGenerator(name = "uuid1", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue(generator = "uuid1")
    private UUID collectorId;

    private String name;

    private String username;

    private String password;

    private String email;

    private String phone;

    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Hub hub;

    @OneToMany(mappedBy = "collector", fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Order> orders;

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;

}
