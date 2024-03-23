package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.entity.AssetType;

import java.util.List;

public interface AssetTypeService {
    List<AssetType> getAllAssetTypes();

    AssetType addNewType(AssetType assetType);
}
