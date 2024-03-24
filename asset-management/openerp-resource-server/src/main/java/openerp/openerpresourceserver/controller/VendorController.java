package openerp.openerpresourceserver.controller;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.entity.Vendor;
import openerp.openerpresourceserver.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
