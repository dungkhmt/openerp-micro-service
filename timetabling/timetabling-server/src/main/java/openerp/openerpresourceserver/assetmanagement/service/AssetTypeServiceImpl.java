package openerp.openerpresourceserver.assetmanagement.service;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.assetmanagement.entity.AssetType;
import openerp.openerpresourceserver.assetmanagement.repo.AssetTypeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor(onConstructor_ = @Autowired)
public class AssetTypeServiceImpl implements AssetTypeService{
    private AssetTypeRepo assetTypeRepo;

    @Override
    public List<AssetType> getAllAssetTypes() {
        return assetTypeRepo.getAllByLastUpdate();
    }

    @Override
    public Optional<AssetType> getTypeById(Integer Id) {
        Optional<AssetType> type = assetTypeRepo.findById(Id);
        return type;
    }

    @Override
    public AssetType addNewType(AssetType assetType) {
        AssetType newType = new AssetType();
        newType.setName(assetType.getName());
        newType.setCode_prefix(assetType.getCode_prefix());
        newType.setDescription(assetType.getDescription());
        newType.setNum_assets(0);

        Date currentDate = new Date();
        newType.setSince(currentDate);
        newType.setLast_updated(currentDate);
        return assetTypeRepo.save(newType);
    }

    @Override
    public AssetType editType(Integer Id, AssetType assetType) {
        AssetType foundType = assetTypeRepo.findById(Id).get();
        foundType.setName(assetType.getName());
        foundType.setDescription(assetType.getDescription());
        foundType.setCode_prefix(assetType.getCode_prefix());

        Date currentDate = new Date();
        foundType.setLast_updated(currentDate);
        return assetTypeRepo.save(foundType);
    }

    @Override
    public void deleteType(Integer Id) {
        Optional<AssetType> foundType = assetTypeRepo.findById(Id);
        if(foundType.isPresent()){
            assetTypeRepo.deleteById(Id);
        }
    }

    @Override
    public List<Integer> getTopTypes() {
        return assetTypeRepo.getTopTypes();
    }
}
