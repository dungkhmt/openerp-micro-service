package openerp.openerpresourceserver.controller;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.entity.Asset;
import openerp.openerpresourceserver.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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

    @GetMapping("/get-all-available")
    public ResponseEntity<?> getAllAvailableAssets(){
        List<Asset> assets = assetService.getAllAvailableAssets();
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(assets);
    }

    @GetMapping("/get-all-inuse")
    public ResponseEntity<?> getAllInuseAssets(){
        List<Asset> assets = assetService.getAllInuseAssets();
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(assets);
    }

    @PostMapping("/add-new")
    public ResponseEntity<?> addNewAsset(@RequestBody Asset asset){
        Asset newAsset = assetService.addNewAsset(asset);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(newAsset);
    }

    @PutMapping("/edit/{Id}")
    public ResponseEntity<?> editAsset(@PathVariable Integer Id, @RequestBody Asset asset){
        Asset foundAsset = assetService.editAsset(Id, asset);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(foundAsset);
    }

    @DeleteMapping("/delete/{Id}")
    public ResponseEntity<?> deleteAsset(@PathVariable Integer Id){
        assetService.deleteAsset(Id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/get-by-user")
    public ResponseEntity<?> getAllUserAssets(Principal principal){
        String user_id = principal.getName();
        List<Asset> assets = assetService.getAllUserAssets(user_id);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(assets);
    }

    @PutMapping("/assign/{Id}/{userId}")
    public ResponseEntity<?> assignAsset(@PathVariable Integer Id, @PathVariable String userId, Principal principal){
        String admin_id = principal.getName();
        Asset asset = assetService.assignAsset(Id, userId, admin_id);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(asset);
    }

    @PutMapping("/revoke/{Id}/{userId}")
    public ResponseEntity<?> revokeAsset(@PathVariable Integer Id, @PathVariable String userId, Principal principal){
        String admin_id = principal.getName();
        Asset asset = assetService.revokeAsset(Id, userId, admin_id);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(asset);
    }
}
