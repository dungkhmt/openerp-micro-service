package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.entity.Asset;

import java.util.List;

public interface AssetService {
    List<Asset> getAllAssets();

    Asset addNewAsset(Asset asset);
}
