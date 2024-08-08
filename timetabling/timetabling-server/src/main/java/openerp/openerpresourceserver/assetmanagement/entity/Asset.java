package openerp.openerpresourceserver.assetmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "asset_management_asset")
public class Asset {
    @Id
    @Column(name = "asset_id", updatable = false, nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String admin_id;

    private String assignee_id;

    private BigDecimal price;

    private String image;

    private Boolean is_deprecated;

    private Integer type_id;

    private Integer status_id;

    private Integer location_id;

    private Integer vendor_id;

    private Date active_date;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String data;

    @CreatedDate
    private Date since;

    @LastModifiedDate
    private Date last_updated;
}
