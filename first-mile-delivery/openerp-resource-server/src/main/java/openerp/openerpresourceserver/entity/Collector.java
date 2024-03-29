package openerp.openerpresourceserver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fmd_collector")
@Builder
public class Collector {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "collector_id")
    private UUID id;

    private String collectorName;

    private String userId;

    private String hubId;

    @OneToMany( fetch = FetchType.EAGER, mappedBy = "id")
    private List<Route> routes;

    @CreatedDate
    @Column(name = "created_stamp")
    private Date createdDate;

    @LastModifiedDate
    @Column(name = "last_updated_stamp")
    private Date lastModifiedDate;
}
