package openerp.openerpresourceserver.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import openerp.openerpresourceserver.dto.request.SupplierCreateRequest;
import openerp.openerpresourceserver.entity.Supplier;
import openerp.openerpresourceserver.repository.SupplierRepository;

@Service
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    public Page<Supplier> searchSuppliers(String search, Pageable pageable) {
		if (search != null && !search.trim().isEmpty()) {
			return supplierRepository.findByNameContainingIgnoreCase(search, pageable);
		}
		return supplierRepository.findAll(pageable);
    }

    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll(Sort.by("name"));
    }
    
    public Optional<Supplier> getSupplierById(UUID id) {
        return supplierRepository.findById(id);
    }


    public Supplier createSupplier(SupplierCreateRequest dto) {
        Supplier supplier = Supplier.builder()
                .name(dto.getName())
                .address(dto.getAddress())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .dateUpdated(LocalDateTime.now())
                .build();
        return supplierRepository.save(supplier);
    }

    public Supplier updateSupplier(Supplier dto) {
        Supplier supplier = supplierRepository.findById(dto.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        supplier.setName(dto.getName());
        supplier.setAddress(dto.getAddress());
        supplier.setEmail(dto.getEmail());
        supplier.setPhone(dto.getPhone());
        supplier.setDateUpdated(LocalDateTime.now());

        return supplierRepository.save(supplier);
    }

}
