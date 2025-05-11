package openerp.openerpresourceserver.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import openerp.openerpresourceserver.dto.request.CustomerAddressCreateRequest;
import openerp.openerpresourceserver.entity.AddressDistance;
import openerp.openerpresourceserver.entity.AddressType;
import openerp.openerpresourceserver.entity.CustomerAddress;
import openerp.openerpresourceserver.entity.Warehouse;
import openerp.openerpresourceserver.repository.AddressDistanceRepository;
import openerp.openerpresourceserver.repository.CustomerAddressRepository;
import openerp.openerpresourceserver.repository.WarehouseRepository;

@Service
public class CustomerAddressService {

	@Autowired
	private WarehouseRepository warehouseRepository;
	
    @Autowired
    private CustomerAddressRepository customerAddressRepository;
    
    @Autowired
    private AddressDistanceRepository addressDistanceRepository;

    // Lấy danh sách địa chỉ theo userLoginId
    public List<CustomerAddress> getCustomerAddressesByUserLoginId(String userLoginId) {
        return customerAddressRepository.findByUserLoginId(userLoginId);
    }
    
    @Transactional
    public CustomerAddress createCustomerAddress(CustomerAddressCreateRequest request, String userLoginId ) {
        CustomerAddress customerAddress = CustomerAddress.builder()
                .userLoginId(userLoginId)
                .addressName(request.getAddressName())
                .longitude(request.getLongitude())
                .latitude(request.getLatitude())
                .build();

        CustomerAddress saved = customerAddressRepository.save(customerAddress);
        UUID savedId = saved.getCustomerAddressId(); 

        List<Warehouse> warehouses = warehouseRepository.findAll();
        List<CustomerAddress> existingCustomers = customerAddressRepository.findAll();

        List<AddressDistance> distances = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (Warehouse wh : warehouses) {
            UUID whId = wh.getWarehouseId();
            distances.add(AddressDistance.builder()
                    .fromLocationId(whId)
                    .fromLocationType(AddressType.WAREHOUSE)
                    .toLocationId(savedId)
                    .toLocationType(AddressType.CUSTOMER)
                    .distance(0)
                    .createdStamp(now)
                    .lastUpdatedStamp(now)
                    .build());

            distances.add(AddressDistance.builder()
                    .fromLocationId(savedId)
                    .fromLocationType(AddressType.CUSTOMER)
                    .toLocationId(whId)
                    .toLocationType(AddressType.WAREHOUSE)
                    .distance(0)
                    .createdStamp(now)
                    .lastUpdatedStamp(now)
                    .build());
        }

        for (CustomerAddress existing : existingCustomers) {
            UUID existingId = existing.getCustomerAddressId();
            if (!existingId.equals(savedId)) {
                distances.add(AddressDistance.builder()
                        .fromLocationId(existingId)
                        .fromLocationType(AddressType.CUSTOMER)
                        .toLocationId(savedId)
                        .toLocationType(AddressType.CUSTOMER)
                        .distance(0)
                        .createdStamp(now)
                        .lastUpdatedStamp(now)
                        .build());

                distances.add(AddressDistance.builder()
                        .fromLocationId(savedId)
                        .fromLocationType(AddressType.CUSTOMER)
                        .toLocationId(existingId)
                        .toLocationType(AddressType.CUSTOMER)
                        .distance(0)
                        .createdStamp(now)
                        .lastUpdatedStamp(now)
                        .build());
            }
        }

        addressDistanceRepository.saveAll(distances);
        return saved;
    }

    
    public List<CustomerAddress> getCustomerAddressesByIds(List<UUID> ids) {
        return customerAddressRepository.findAllById(ids);
    }
}

