package openerp.openerpresourceserver.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import openerp.openerpresourceserver.dto.request.CustomerAddressCreateRequest;
import openerp.openerpresourceserver.entity.CustomerAddress;
import openerp.openerpresourceserver.repository.CustomerAddressRepository;

@Service
public class CustomerAddressService {

    @Autowired
    private CustomerAddressRepository customerAddressRepository;

    // Lấy danh sách địa chỉ theo userLoginId
    public List<CustomerAddress> getCustomerAddressesByUserLoginId(String userLoginId) {
        return customerAddressRepository.findByUserLoginId(userLoginId);
    }
    
    public CustomerAddress createCustomerAddress(CustomerAddressCreateRequest request) {
        // Tạo một đối tượng CustomerAddress từ request
        CustomerAddress customerAddress = CustomerAddress.builder()
                .userLoginId(request.getUserLoginId())
                .addressName(request.getAddressName())
                .longitude(request.getLongitude())
                .latitude(request.getLatitude())
                .build();

        // Lưu vào database
        return customerAddressRepository.save(customerAddress);
    }
}

