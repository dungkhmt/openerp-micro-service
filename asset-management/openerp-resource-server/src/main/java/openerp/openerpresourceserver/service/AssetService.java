package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.entity.Asset;

import java.util.List;

public interface AssetService {
    List<Asset> getAllAssets();

    List<Asset> getAllAvailableAssets();

    List<Asset> getAllInuseAssets();

    List<Asset> getAllUserAssets(String user_id);

    Asset addNewAsset(Asset asset);

    Asset editAsset(Integer Id, Asset asset);

    void deleteAsset(Integer Id);

    Asset assignAsset(Integer Id, String user_id, String admin_id);

    Asset revokeAsset(Integer Id, String user_id, String admin_id);
}
