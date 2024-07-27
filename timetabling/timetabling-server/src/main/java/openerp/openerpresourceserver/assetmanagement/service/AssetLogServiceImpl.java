package openerp.openerpresourceserver.assetmanagement.service;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.assetmanagement.entity.Asset;
import openerp.openerpresourceserver.assetmanagement.entity.AssetLog;
import openerp.openerpresourceserver.assetmanagement.repo.AssetLogRepo;
import openerp.openerpresourceserver.assetmanagement.repo.AssetRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@AllArgsConstructor(onConstructor_ = @Autowired)
public class AssetLogServiceImpl implements AssetLogService {
    private AssetLogRepo assetLogRepo;
    private AssetRepo assetRepo;

    @Override
    public AssetLog createNewAssetLog(Integer asset_id, String user_id, String action) {
        AssetLog assetLog = new AssetLog();
        assetLog.setAsset_id(asset_id);
        Asset asset = assetRepo.findById(asset_id).get();
        assetLog.setUser_id(user_id);
        assetLog.setAction(action);
        assetLog.setName(user_id + " " + action  + " " + asset.getName());
        assetLog.setSince(new Date());
        assetLog.setLast_updated(new Date());
        assetLog.setDescription(user_id + " " + action  + " " + asset.getName());
        return assetLogRepo.save(assetLog);
    }
}

