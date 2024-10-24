package openerp.openerpresourceserver.userfeaturestore.repo;

import openerp.openerpresourceserver.userfeaturestore.entity.CompositeUserFeatureId;
import openerp.openerpresourceserver.userfeaturestore.entity.UserFeatures;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserFeaturesRepo extends JpaRepository<UserFeatures, CompositeUserFeatureId> {
    UserFeatures findByUserIdAndFeatureId(String userId, String featureId);
}
