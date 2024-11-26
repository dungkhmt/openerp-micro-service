package openerp.openerpresourceserver.userfeaturestore.entity;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class CompositeUserFeatureId {
    private String userId;
    private String featureId;
}
