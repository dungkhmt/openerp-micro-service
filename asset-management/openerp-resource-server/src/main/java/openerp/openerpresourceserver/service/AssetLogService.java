package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.entity.AssetLog;

public interface AssetLogService {
    AssetLog createNewAssetLog(Integer asset_id, String user_id, String action);
}
