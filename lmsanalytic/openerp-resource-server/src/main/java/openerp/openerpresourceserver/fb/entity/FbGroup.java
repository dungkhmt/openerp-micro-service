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
@Table(name = "fb_group")
public class FbGroup {
    @Id
    @Column(name="id")
    private String id;

    @Column(name="group_name")
    private String groupName;
    @Column(name="group_type")
    private String groupType;

    @Column(name="number_members")
    private int numberMembers;

    @Column(name="link")
    private String link;

    @Column(name="create_stamp")
    private Date createStamp;

}
