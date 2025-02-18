package openerp.openerpresourceserver.fb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fb_user")
public class FbUser {
    @Id
    @Column(name="id")
    private String id;

    @Column(name="group_id")
    private String groupId;

    @Column(name="name")
    private String Name;

    @Column(name="link")
    private String link;

    @Column(name="create_stamp")
    private Date createStamp;

    @Column(name = "last_updated")
    private Date lastUpdated;
}
