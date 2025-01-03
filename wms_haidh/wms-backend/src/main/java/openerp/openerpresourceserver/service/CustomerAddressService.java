package openerp.openerpresourceserver.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import openerp.openerpresourceserver.entity.CustomerAddress;
import openerp.openerpresourceserver.entity.Order;
import openerp.openerpresourceserver.repository.CustomerAddressRepository;

@Service
public class CustomerAddressService {
    private final OrderService orderService;
    private final CustomerAddressRepository customerAddressRepository;

    public CustomerAddressService(OrderService orderService, CustomerAddressRepository customerAddressRepository) {
        this.orderService = orderService;
        this.customerAddressRepository = customerAddressRepository;
    }

    public CustomerAddress getCustomerAddressByOrderId(UUID orderId) {
        Order order = orderService.getOrderById(orderId);
        return customerAddressRepository.findById(order.getCustomerAddressId())
                .orElseThrow(() -> new RuntimeException("Customer address not found for id: " + order.getCustomerAddressId()));
    }
}

