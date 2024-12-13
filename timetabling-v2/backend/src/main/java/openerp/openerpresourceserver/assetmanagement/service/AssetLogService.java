package openerp.openerpresourceserver.assetmanagement.service;

import openerp.openerpresourceserver.assetmanagement.entity.AssetLog;

public interface AssetLogService {
    AssetLog createNewAssetLog(Integer asset_id, String user_id, String action);
}
