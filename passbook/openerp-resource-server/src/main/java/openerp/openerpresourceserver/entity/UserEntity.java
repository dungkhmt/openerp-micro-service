package openerp.openerpresourceserver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_login")
public class UserEntity {

    @Id
    @Column(name = "user_login_id")
    private String id;

    @Column(name = "created_stamp")
    private Date createdOn;
}
