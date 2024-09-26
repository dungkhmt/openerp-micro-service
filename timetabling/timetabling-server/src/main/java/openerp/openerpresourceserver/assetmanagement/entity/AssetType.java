package openerp.openerpresourceserver.assetmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;
// reuqets muon, request tra
// tu ngay nao den ngay nao
// Principal principal
// qua han thi co thong bao
//
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "asset_management_asset_type")
public class AssetType {
    @Id
    @Column(updatable = false, nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String user_id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, unique = true)
    private String code_prefix;

    private Integer num_assets;

    @CreatedDate
    private Date since;

    @LastModifiedDate
    private Date last_updated;
}
