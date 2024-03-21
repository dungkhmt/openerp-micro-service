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

    @Override
    public Vendor addNewVendor(Vendor vendor) {
        Vendor savedVendor = new Vendor();
        savedVendor.setName(vendor.getName());
        savedVendor.setEmail(vendor.getEmail());
        savedVendor.setPhone(vendor.getPhone());
        savedVendor.setAddress(vendor.getAddress());
        savedVendor.setDescription(vendor.getDescription());
        savedVendor.setImage(vendor.getImage());
        return savedVendor;
    }
}
