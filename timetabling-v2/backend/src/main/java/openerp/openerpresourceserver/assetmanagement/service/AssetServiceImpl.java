package openerp.openerpresourceserver.assetmanagement.service;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.assetmanagement.entity.Asset;
import openerp.openerpresourceserver.assetmanagement.entity.AssetType;
import openerp.openerpresourceserver.assetmanagement.entity.Location;
import openerp.openerpresourceserver.assetmanagement.entity.Vendor;
import openerp.openerpresourceserver.assetmanagement.repo.AssetRepo;
import openerp.openerpresourceserver.assetmanagement.repo.AssetTypeRepo;
import openerp.openerpresourceserver.assetmanagement.repo.LocationRepo;
import openerp.openerpresourceserver.assetmanagement.repo.VendorRepo;
import openerp.openerpresourceserver.assetmanagement.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor(onConstructor_ = @Autowired)
public class AssetServiceImpl implements AssetService{
    private AssetRepo assetRepo;
    private AssetTypeRepo assetTypeRepo;
    private LocationRepo locationRepo;
    private VendorRepo vendorRepo;

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
        Integer num_assets = assetType.getNum_assets();
        assetType.setNum_assets(num_assets + 1);

        String prefix = assetType.getCode_prefix();
        Utils utils = new Utils();
        newAsset.setCode(prefix + "-" + utils.generateRandomHash(8));

        Location location = locationRepo.findById(asset.getLocation_id()).get();
        location.setNum_assets(location.getNum_assets() + 1);

        Vendor vendor = vendorRepo.findById(asset.getVendor_id()).get();
        vendor.setNum_assets(vendor.getNum_assets() + 1);

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
        assetType.setLast_updated(currentDate);
        location.setLast_updated(currentDate);
        vendor.setLast_updated(currentDate);
        assetTypeRepo.save(assetType);
        locationRepo.save(location);
        vendorRepo.save(vendor);
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
        Asset asset = assetRepo.findById(Id).get();
        if(asset.getStatus_id() == REPAIR || asset.getStatus_id() == INUSE) {
            return;
        }

        AssetType assetType = assetTypeRepo.findById(asset.getType_id()).get();
        Integer num_assets = assetType.getNum_assets();
        assetType.setNum_assets(num_assets - 1);

        Location location = locationRepo.findById(asset.getLocation_id()).get();
        location.setNum_assets(location.getNum_assets() - 1);

        Vendor vendor = vendorRepo.findById(asset.getVendor_id()).get();
        vendor.setNum_assets(vendor.getNum_assets() - 1);

        assetTypeRepo.save(assetType);
        locationRepo.save(location);
        vendorRepo.save(vendor);
        assetRepo.delete(asset);
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

    @Override
    public Asset repairAsset(Integer Id, String admin_id, Boolean is_repair) {
        Asset foundAsset = assetRepo.findById(Id).get();
        if(!foundAsset.getAdmin_id().equals(admin_id)){
            return null;
        }
        if(is_repair){
            foundAsset.setStatus_id(REPAIR);
        } else {
            foundAsset.setStatus_id(AVAILABLE);
        }

        foundAsset.setLast_updated(new Date());
        return assetRepo.save(foundAsset);
    }

    @Override
    public Asset deprecatedAsset(Integer Id, String admin_id) {
        Asset foundAsset = assetRepo.findById(Id).get();
        if(foundAsset.getStatus_id().equals(DEPRECATED)){
            return null;
        }
        if(!foundAsset.getAdmin_id().equals(admin_id)){
            return null;
        }
        foundAsset.setStatus_id(DEPRECATED);
        foundAsset.setLast_updated(new Date());
        return assetRepo.save(foundAsset);
    }

    @Override
    public List<String> getTopAssignUsers() {
        return assetRepo.getTopAssignUsers();
    }

    @Override
    public List<String> getTopAdminUsers() {
        return assetRepo.getTopAdminUsers();
    }

    @Override
    public List<Asset> getByAdminUser(String user_id) {
        return assetRepo.findByAdminId(user_id);
    }

    @Override
    public List<Asset> getByTypes(Integer type_id) {
        return assetRepo.findByTypes(type_id);
    }

    @Override
    public List<Asset> getAssignToMe(String user_id) {
        return assetRepo.assignToMe(user_id);
    }

    @Override
    public List<Asset> getManageByMe(String user_id) {
        return assetRepo.manageByMe(user_id);
    }
}
