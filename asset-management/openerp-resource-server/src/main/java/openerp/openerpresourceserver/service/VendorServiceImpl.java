package openerp.openerpresourceserver.service;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.entity.Vendor;
import openerp.openerpresourceserver.repo.VendorRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor(onConstructor_ = @Autowired)
public class VendorServiceImpl implements VendorService{
    private VendorRepo vendorRepo;

    @Override
    public List<Vendor> getAllVendors() {
        List<Vendor> vendors = vendorRepo.findAll();
        return vendors;
    }
}
