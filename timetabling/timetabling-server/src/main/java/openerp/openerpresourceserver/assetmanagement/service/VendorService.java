package openerp.openerpresourceserver.assetmanagement.service;

import openerp.openerpresourceserver.assetmanagement.entity.Vendor;

import java.util.List;
import java.util.Optional;

public interface VendorService {
    List<Vendor> getAllVendors();

    Optional<Vendor> getVendorById(Integer Id);

    Vendor addNewVendor(Vendor vendor);

    Vendor editVendor(Integer Id, Vendor vendor);

    void deleteVendor(Integer Id);
}
