package openerp.openerpresourceserver.controller;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.entity.AssetType;
import openerp.openerpresourceserver.service.AssetTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

    @GetMapping("/get/{Id}")
    public ResponseEntity<?> getTypeById(@PathVariable Integer Id){
        Optional<AssetType> assetType = assetTypeService.getTypeById(Id);
        return ResponseEntity
            .status(HttpStatus.FOUND)
            .body(assetType);
    }

    @PostMapping("/add-new")
    public ResponseEntity<?> addNewType(@RequestBody AssetType assetType){
        AssetType newType = assetTypeService.addNewType(assetType);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(newType);
    }
}
