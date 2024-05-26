package openerp.openerpresourceserver.service;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.entity.Asset;
import openerp.openerpresourceserver.entity.AssetType;
import openerp.openerpresourceserver.repo.AssetRepo;
import openerp.openerpresourceserver.repo.AssetTypeRepo;
import openerp.openerpresourceserver.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor(onConstructor_ = @Autowired)
public class AssetServiceImpl implements AssetService{
    private AssetRepo assetRepo;
    private AssetTypeRepo assetTypeRepo;

    private final Integer AVAILABLE = 1;
    private final Integer INUSE = 2;
    private final Integer REPAIR = 3;
    private final Integer DEPRECATED = 4;

    @Override
    public List<Asset> getAllAssets() {
        return assetRepo.getAllByLastUpdate();
    }

    @Override
    public Asset getById(Integer id) {
        return assetRepo.findById(id).orElse(null);
    }

    @Override
    public List<Asset> getAllAvailableAssets() {
        return assetRepo.findByStatusId(AVAILABLE);
    }

    @Override
    public List<Asset> getAllInuseAssets() {
        return assetRepo.findByStatusId(INUSE);
    }

    @Override
    public List<Asset> getAllUserAssets(String user_id) {
        return assetRepo.findByAssigneeId(user_id);
    }

    @Override
    public Asset addNewAsset(Asset asset) {
        Asset newAsset = new Asset();
        newAsset.setName(asset.getName());
        AssetType assetType = assetTypeRepo.findById(asset.getType_id()).get();
        newAsset.setType_id(assetType.getId());
        System.out.println(assetType);
        String prefix = assetType.getCode_prefix();
        Utils utils = new Utils();
        newAsset.setCode(prefix + "-" + utils.generateRandomHash(8));
        newAsset.setAdmin_id(asset.getAdmin_id());
        newAsset.setStatus_id(AVAILABLE);
        newAsset.setAssignee_id(asset.getAssignee_id());
        newAsset.setLocation_id(asset.getLocation_id());
        newAsset.setVendor_id(asset.getVendor_id());
        newAsset.setActive_date(asset.getActive_date());
        newAsset.setDescription(asset.getDescription());

        Date currentDate = new Date();
        newAsset.setSince(currentDate);
        newAsset.setLast_updated(currentDate);
        return assetRepo.save(newAsset);
    }

    @Override
    public Asset editAsset(Integer Id, Asset asset) {
        Asset foundAsset = assetRepo.findById(Id).get();
        foundAsset.setName(asset.getName());
        foundAsset.setDescription(asset.getDescription());

        foundAsset.setVendor_id(asset.getVendor_id());
        foundAsset.setLocation_id(asset.getLocation_id());
        foundAsset.setImage(asset.getImage());
        foundAsset.setAssignee_id(asset.getAssignee_id());

        Integer typeId = asset.getType_id();
        AssetType type = assetTypeRepo.findById(typeId).get();
        String typePrefix = type.getCode_prefix();
        foundAsset.setType_id(typeId);
        String code = foundAsset.getCode();
        String prefix = code.split("-")[1];
        foundAsset.setCode(typePrefix + "-" + prefix);

        foundAsset.setLast_updated(new Date());
        return assetRepo.save(foundAsset);
    }

    @Override
    public void deleteAsset(Integer Id) {
        Optional<Asset> asset = assetRepo.findById(Id);
        if(asset.isPresent()){
            assetRepo.deleteById(Id);
        }
    }

    @Override
    public Asset assignAsset(Integer Id, String user_id, String admin_id) {
        Asset foundAsset = assetRepo.findById(Id).get();
        if(!foundAsset.getAssignee_id().equals(admin_id)){
            return null;
        }
        if(!foundAsset.getStatus_id().equals(AVAILABLE)){
            return null;
        }
        foundAsset.setStatus_id(INUSE);
        foundAsset.setAssignee_id(user_id);
        foundAsset.setLast_updated(new Date());
        return assetRepo.save(foundAsset);
    }

    @Override
    public Asset revokeAsset(Integer Id, String user_id, String admin_id) {
        Asset foundAsset = assetRepo.findById(Id).get();
        if(!foundAsset.getAssignee_id().equals(admin_id)){
            return null;
        }
        if(!foundAsset.getStatus_id().equals(INUSE)){
            return null;
        }
        foundAsset.setStatus_id(AVAILABLE);
        foundAsset.setAssignee_id(null);
        foundAsset.setLast_updated(new Date());
        return assetRepo.save(foundAsset);
    }
}
