package openerp.openerpresourceserver.assetmanagement.service;

import openerp.openerpresourceserver.assetmanagement.entity.AssetType;

import java.util.List;
import java.util.Optional;

public interface AssetTypeService {
    List<AssetType> getAllAssetTypes();

    Optional<AssetType> getTypeById(Integer Id);

    AssetType addNewType(AssetType assetType);

    AssetType editType(Integer Id, AssetType assetType);

    void deleteType(Integer Id);

    List<Integer> getTopTypes();
}
