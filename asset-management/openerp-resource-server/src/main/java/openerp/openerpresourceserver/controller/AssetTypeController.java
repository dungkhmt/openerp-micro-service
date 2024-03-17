package openerp.openerpresourceserver.controller;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.entity.AssetType;
import openerp.openerpresourceserver.service.AssetTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/asset-type")
public class AssetTypeController {
    private AssetTypeService assetTypeService;

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllAssetTypes(){
        List<AssetType> assetTypes = assetTypeService.getAllAssetTypes();
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(assetTypes);
    }
}
