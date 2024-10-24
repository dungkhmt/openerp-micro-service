package openerp.openerpresourceserver.userfeaturestore.service;

import openerp.openerpresourceserver.userfeaturestore.entity.UserFeatures;
import openerp.openerpresourceserver.userfeaturestore.repo.UserFeaturesRepo;

import java.util.List;

public interface UserFeaturesService {
    UserFeatures createUserFeature(String userId, String featureId, float value);
    List<UserFeatures> findAll();
}
