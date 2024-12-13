package openerp.openerpresourceserver.assetmanagement.service;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.assetmanagement.entity.Vendor;
import openerp.openerpresourceserver.assetmanagement.repo.VendorRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor(onConstructor_ = @Autowired)
public class VendorServiceImpl implements VendorService{
    private VendorRepo vendorRepo;

    @Override
    public List<Vendor> getAllVendors() {
        return vendorRepo.getAllByLastUpdate();
    }

    @Override
    public Optional<Vendor> getVendorById(Integer Id) {
        Optional<Vendor> vendor = vendorRepo.findById(Id);
        return vendor;
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
        savedVendor.setUrl(vendor.getUrl());
        savedVendor.setNum_assets(0);

        Date currentDate = new Date();
        savedVendor.setSince(currentDate);
        savedVendor.setLast_updated(currentDate);

        return vendorRepo.save(savedVendor);
    }

    @Override
    public Vendor editVendor(Integer Id, Vendor vendor) {
        Vendor foundVendor = vendorRepo.findById(Id).get();
        foundVendor.setName(vendor.getName());
        foundVendor.setDescription(vendor.getDescription());
        foundVendor.setAddress(vendor.getAddress());
        foundVendor.setEmail(vendor.getEmail());
        foundVendor.setImage(vendor.getImage());
        foundVendor.setUrl(vendor.getUrl());
        foundVendor.setLast_updated(new Date());

        return vendorRepo.save(foundVendor);
    }

    @Override
    public void deleteVendor(Integer Id) {
        Optional<Vendor> vendor = vendorRepo.findById(Id);
        if(vendor.isPresent()){
            vendorRepo.deleteById(Id);
        }
    }
}
