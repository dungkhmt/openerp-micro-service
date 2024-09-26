package openerp.openerpresourceserver.assetmanagement.controller;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.assetmanagement.entity.Vendor;
import openerp.openerpresourceserver.assetmanagement.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/vendor")
public class VendorController {
    private VendorService vendorService;

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllVendors(){
        List<Vendor> vendors = vendorService.getAllVendors();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(vendors);
    }

    @GetMapping("/get/{Id}")
    public ResponseEntity<?> getVendorById(@PathVariable Integer Id){
        Optional<Vendor> vendor = vendorService.getVendorById(Id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(vendor);
    }

    @PostMapping("/add-new")
    public ResponseEntity<?> addNewVendor(@RequestBody Vendor vendor){
        Vendor newVendor = vendorService.addNewVendor(vendor);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(newVendor);
    }

    @PutMapping("/edit/{Id}")
    public ResponseEntity<?> editVendor(@PathVariable Integer Id, @RequestBody Vendor vendor){
        Vendor savedVendor = vendorService.editVendor(Id, vendor);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(savedVendor);
    }

    @DeleteMapping("/delete/{Id}")
    public ResponseEntity<?> deleteVendor(@PathVariable Integer Id){
        vendorService.deleteVendor(Id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
