package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.entity.Vendor;

import java.util.List;

public interface VendorService {
    List<Vendor> getAllVendors();

    Vendor addNewVendor(Vendor vendor);
}
