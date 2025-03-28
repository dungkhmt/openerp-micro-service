package openerp.openerpresourceserver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "asset_management_vendor")
public class Vendor {
    @Id
    @Column(updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String user_id;

    private String name;

    private String phone;

    private String email;

    private String address;

    private String url;

    private String image;

    @Column(columnDefinition = "TEXT")
    private  String description;

    private Integer num_assets;

    @CreatedDate
    private Date since;

    @LastModifiedDate
    private Date last_updated;
}
