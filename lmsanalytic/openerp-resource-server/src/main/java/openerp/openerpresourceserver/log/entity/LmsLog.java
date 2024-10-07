package openerp.openerpresourceserver.log.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "lms_log")
public class LmsLog {
    public static final String ACTION_TYPE_GET_ALL_USERS = "GET_ALL_USERS";
    public static final String ACTION_TYPE_GET_ALL_CONTEST = "GET_ALL_CONTESTS";
    public static final String ACTION_TYPE_GET_ALL_PROBLEMS = "GET_ALL_PROBLEMS";


    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name="user_id")
    private String userId;

    @Column(name="action_type")
    private String actionType;

    @Column(name="param1")
    private String param1;

    @Column(name="param2")
    private String param2;
    @Column(name="param3")
    private String param3;
    @Column(name="param4")
    private String param4;
    @Column(name="param5")
    private String param5;

    @Column(name="description")
    private String description;

    @Column(name="created_stamp")
    private Date createdStamp;



}
