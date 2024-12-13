package openerp.openerpresourceserver.assetmanagement.service;

import openerp.openerpresourceserver.assetmanagement.entity.Asset;

import java.util.List;

public interface AssetService {
    List<Asset> getAllAssets();

    Asset getById(Integer id);

    List<Asset> getAllAvailableAssets();

    List<Asset> getAllInuseAssets();

    List<Asset> getAllUserAssets(String user_id);

    Asset addNewAsset(Asset asset);

    Asset editAsset(Integer Id, Asset asset);

    void deleteAsset(Integer Id);

    Asset assignAsset(Integer Id, String user_id, String admin_id);

    Asset revokeAsset(Integer Id, String user_id, String admin_id);

    Asset repairAsset(Integer Id, String admin_id, Boolean is_repair);

    Asset deprecatedAsset(Integer Id, String admin_id);

    List<String> getTopAssignUsers();

    List<String> getTopAdminUsers();

    List<Asset> getByAdminUser(String user_id);

    List<Asset> getByTypes(Integer type_id);

    List<Asset> getAssignToMe(String user_id);

    List<Asset> getManageByMe(String user_id);
}
