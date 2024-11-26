package openerp.openerpresourceserver.userfeaturestore.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.userfeaturestore.entity.UserFeatures;
import openerp.openerpresourceserver.userfeaturestore.repo.UserFeaturesRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Log4j2
@AllArgsConstructor(onConstructor_ = @Autowired)
@Service

public class UserFeaturesServiceImpl implements  UserFeaturesService{
    private UserFeaturesRepo userFeaturesRepo;
    @Override
    public UserFeatures createUserFeature(String userId, String featureId, float value) {
        UserFeatures uf = new UserFeatures();
        uf.setFeatureId(featureId);
        uf.setUserId(userId);
        uf.setValue(value);
        uf.setCreatedStamp(new Date());
        uf.setLastUpdatedStamp(new Date());
        //uf.setStatus("CREATED");
        uf = userFeaturesRepo.save(uf);
        return uf;
    }

    @Override
    public List<UserFeatures> findAll() {
        return userFeaturesRepo.findAll();
    }
}
