package openerp.openerpresourceserver.userfeaturestore.entity;


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
@Table(name = "user_features")
@IdClass(CompositeUserFeatureId.class)
public class UserFeatures {
    public static final String FEATURE_NUMBER_ACTIONS = "NUMBER_ACTIONS";
    //public static final String FEATURE_NUMBER_ACTIONS_

    @Id
    @Column(name="user_id")
    private String userId;

    @Id
    @Column(name="feature_id")
    private String featureId;

    @Column(name="value")
    private float value;

    @Column(name="status")
    private String status;

    @Column(name="last_updated_stamp")
    private Date lastUpdatedStamp;

    @Column(name="created_stamp")
    private Date createdStamp;
}
