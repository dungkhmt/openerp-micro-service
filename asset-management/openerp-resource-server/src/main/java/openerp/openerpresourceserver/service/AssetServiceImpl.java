package openerp.openerpresourceserver.service;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.entity.Asset;
import openerp.openerpresourceserver.entity.AssetType;
import openerp.openerpresourceserver.repo.AssetRepo;
import openerp.openerpresourceserver.repo.AssetTypeRepo;
import openerp.openerpresourceserver.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor(onConstructor_ = @Autowired)
public class AssetServiceImpl implements AssetService{
    private AssetRepo assetRepo;
    private AssetTypeRepo assetTypeRepo;

    @Override
    public List<Asset> getAllAssets() {
        List<Asset> assets = assetRepo.findAll();
        return assets;
    }

    @Override
    public Asset addNewAsset(Asset asset) {
        Asset newAsset = new Asset();
        newAsset.setName(asset.getName());
        AssetType assetType = assetTypeRepo.findById(asset.getType_id()).get();
        String prefix = assetType.getCodePrefix();
        Utils utils = new Utils();
        newAsset.setCode(prefix + "-" + utils.generateRandomHash(6));
        newAsset.setAssignee_id(asset.getAssignee_id());
        newAsset.setLocation_id(asset.getLocation_id());
        newAsset.setVendor_id(asset.getVendor_id());
        newAsset.setActive_date(asset.getActive_date());
        newAsset.setDescription(asset.getDescription());
        return newAsset;
    }
}
