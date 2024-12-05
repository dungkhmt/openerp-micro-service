package openerp.openerpresourceserver.userfeaturestore.repo;

import openerp.openerpresourceserver.userfeaturestore.entity.CompositeUserFeatureId;
import openerp.openerpresourceserver.userfeaturestore.entity.UserFeatures;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserFeaturesRepo extends JpaRepository<UserFeatures, CompositeUserFeatureId> {
    UserFeatures findByUserIdAndFeatureId(String userId, String featureId);

    //@Modifying
    //@Query("delete from user_features UF where UF.feature_id = :feature_id")
    //void deleteUserFeaturesSubmissionCount(@Param("feature_id") String feature_id);

    void deleteByFeatureId(String featureId);

    @Query(value = "select count(*) from user_features where feature_id = :feature_id",nativeQuery = true)
    int getCountFeatures(@Param("feature_id") String feature_id);

}
