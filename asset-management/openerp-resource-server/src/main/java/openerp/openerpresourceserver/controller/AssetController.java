package openerp.openerpresourceserver.controller;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.entity.Asset;
import openerp.openerpresourceserver.service.AssetLogService;
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
    private AssetLogService assetLogService;

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllAssets(){
        List<Asset> assets = assetService.getAllAssets();
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(assets);
    }

    @GetMapping("/id/{Id}")
    public ResponseEntity<?> getById(@PathVariable Integer Id){
        Asset asset = assetService.getById(Id);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(asset);
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
        assetLogService.createNewAssetLog(newAsset.getId(), newAsset.getAdmin_id(), "create");
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(newAsset);
    }

    @PutMapping("/edit/{Id}")
    public ResponseEntity<?> editAsset(@PathVariable Integer Id, @RequestBody Asset asset){
        Asset foundAsset = assetService.editAsset(Id, asset);
        assetLogService.createNewAssetLog(foundAsset.getId(), foundAsset.getAdmin_id(), "edit");
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(foundAsset);
    }

    @DeleteMapping("/delete/{Id}")
    public ResponseEntity<?> deleteAsset(@PathVariable Integer Id, Principal principal){
        assetService.deleteAsset(Id);
//        assetLogService.createNewAssetLog(Id, principal.getName(), "delete");
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
        assetLogService.createNewAssetLog(asset.getId(), admin_id, "assign");
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(asset);
    }

    @PutMapping("/revoke/{Id}/{userId}")
    public ResponseEntity<?> revokeAsset(@PathVariable Integer Id, @PathVariable String userId, Principal principal){
        String admin_id = principal.getName();
        Asset asset = assetService.revokeAsset(Id, userId, admin_id);
        assetLogService.createNewAssetLog(asset.getId(), admin_id, "revoke");
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(asset);
    }

    @GetMapping("/top-admin-users")
    public ResponseEntity<?> getTopAdminUsers(){
        List<String> users = assetService.getTopAdminUsers();
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(users);
    }

    @GetMapping("/get-by-admin/{userId}")
    public ResponseEntity<?> getByAdminUser(@PathVariable String userId){
        List<Asset> assets = assetService.getByAdminUser(userId);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(assets);
    }
}
