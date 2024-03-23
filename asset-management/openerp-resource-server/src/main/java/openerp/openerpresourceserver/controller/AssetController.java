package openerp.openerpresourceserver.controller;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.entity.Asset;
import openerp.openerpresourceserver.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/asset")
public class AssetController {
    private AssetService assetService;

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllAssets(){
        List<Asset> assets = assetService.getAllAssets();
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(assets);
    }

    @PostMapping("/add-new")
    public ResponseEntity<?> addNewAsset(Asset asset){
        Asset newAsset = assetService.addNewAsset(asset);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(newAsset);
    }
}
