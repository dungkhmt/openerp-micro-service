package openerp.openerpresourceserver.service;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.entity.AssetType;
import openerp.openerpresourceserver.repo.AssetTypeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor(onConstructor_ = @Autowired)
public class AssetTypeServiceImpl implements AssetTypeService{
    private AssetTypeRepo assetTypeRepo;

    @Override
    public List<AssetType> getAllAssetTypes() {
        List<AssetType> assetTypes = assetTypeRepo.findAll();
        return assetTypes;
    }
}
