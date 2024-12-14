package openerp.openerpresourceserver.assetmanagement.controller;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.assetmanagement.entity.AssetType;
import openerp.openerpresourceserver.assetmanagement.service.AssetTypeService;
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
                .status(HttpStatus.OK)
                .body(assetType);
    }

    @PostMapping("/add-new")
    public ResponseEntity<?> addNewType(@RequestBody AssetType assetType){
        AssetType newType = assetTypeService.addNewType(assetType);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(newType);
    }

    @PutMapping("/edit/{Id}")
    public ResponseEntity<?> editType(@PathVariable Integer Id, @RequestBody AssetType assetType){
        AssetType type = assetTypeService.editType(Id, assetType);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(type);
    }

    @DeleteMapping("/delete/{Id}")
    public ResponseEntity<?> deleteType(@PathVariable Integer Id){
        assetTypeService.deleteType(Id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/get-top-types")
    public ResponseEntity<?> getTopTypes(){
        List<Integer> topTypes = assetTypeService.getTopTypes();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(topTypes);
    }
}
