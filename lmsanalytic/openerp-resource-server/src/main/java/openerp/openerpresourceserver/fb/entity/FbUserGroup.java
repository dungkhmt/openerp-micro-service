package openerp.openerpresourceserver.fb.entity;

import jakarta.persistence.*;
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
@Table(name = "fb_user_group")
@IdClass(CompositeUserGroupId.class)
public class FbUserGroup {
    @Id
    @Column(name="user_id")
    private String userId;

    @Id
    @Column(name="group_id")
    private String groupId;

    @Column(name="create_stamp")
    private Date createStamp;

    @Column(name = "last_updated")
    private Date lastUpdated;

}
