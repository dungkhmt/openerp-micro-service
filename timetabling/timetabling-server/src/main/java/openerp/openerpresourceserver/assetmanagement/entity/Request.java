package openerp.openerpresourceserver.assetmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "asset_management_request")
public class Request {
    @Id
    @Column(updatable = false, nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String user_id;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer status;

    private Integer type;

    private Integer asset_id;

    private String admin_id;

    private Integer parent_id;

    @Column(columnDefinition = "TEXT")
    private String data;

    private Date start_date;

    private Date end_date;

    private Date payback_date;

    @CreatedDate
    private Date since;

    @LastModifiedDate
    private Date last_updated;
}
