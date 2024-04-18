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
@Table(name = "asset_management_request")
public class Request {
    @Id
    @Column(updatable = false, nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer user_id;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer status;

    private String approval_flow;

    @Column(columnDefinition = "TEXT")
    private String approvers_id;

    @Column(columnDefinition = "TEXT")
    private String approvals_id;

    @Column(columnDefinition = "TEXT")
    private String rejections_id;

    @Column(columnDefinition = "TEXT")
    private String data;

    @CreatedDate
    private Date since;

    @LastModifiedDate
    private Date last_updated;
}
