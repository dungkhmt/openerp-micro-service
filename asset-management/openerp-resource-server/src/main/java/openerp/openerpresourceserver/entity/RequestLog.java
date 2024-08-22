package openerp.openerpresourceserver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "asset_management_request_log")
public class RequestLog {
    @Id
    @Column(updatable = false, nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String user_id;

    private Integer request_id;

    private String request_type;

    private String action;

    @Column(columnDefinition = "TEXT")
    private  String description;

    @Column(columnDefinition = "TEXT")
    private String data;

    @CreatedDate
    private Date since;

    @LastModifiedDate
    private Date last_updated;
}
