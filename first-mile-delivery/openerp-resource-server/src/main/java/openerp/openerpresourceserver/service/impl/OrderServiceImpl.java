package openerp.openerpresourceserver.service.impl;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.dto.OrderDTO;
import openerp.openerpresourceserver.entity.Customer;
import openerp.openerpresourceserver.entity.Order;
import openerp.openerpresourceserver.repo.CustomerRepo;
import openerp.openerpresourceserver.repo.OrderRepo;
import openerp.openerpresourceserver.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor(onConstructor_ = @Autowired)
@Service
public class OrderServiceImpl implements OrderService {

    private OrderRepo orderRepo;

    private CustomerRepo customerRepo;

    @Override
    public Order createOrder(OrderDTO orderDTO, String userId) {


        Customer customer = customerRepo.findFirstByUserId(userId).orElse(null);

        if(customer == null){
            return null;
        }

        Order order = Order.builder()
                .id(UUID.randomUUID())
                .customerId(customer.getCustomerId())
                .weight(orderDTO.getWeight())
                .volume(orderDTO.getVolume())
                .fromDateTime(orderDTO.getFromDateTime())
                .toDateTime(orderDTO.getToDateTime())
                .address(orderDTO.getAddress())
                .status(Order.PENDING)
                .build();

        return orderRepo.save(order);

    }

    @Override
    public List<Order> findAllByCustomerId(String userId) {

        Customer customer = customerRepo.findFirstByUserId(userId).orElse(null);


        if(customer == null){
            return null;
        }

        return orderRepo.findByCustomerId(customer.getCustomerId());
    }



    @Override
    public List<Order> findAll() {
        return orderRepo.findAll();
    }

    @Override
    public Order updateOrder(UUID orderId ,OrderDTO orderDTO, String userId)  {

        Order order = orderRepo.findById(orderId).orElse(null);

        if(order == null){
            return null;
        }

        order.setWeight(orderDTO.getWeight());
        order.setVolume(orderDTO.getVolume());
        order.setFromDateTime(orderDTO.getFromDateTime());
        order.setToDateTime(orderDTO.getToDateTime());
        order.setAddress(orderDTO.getAddress());
        order.setStatus(orderDTO.getStatus());



        return orderRepo.save(order);
    }

    @Override
    public boolean deleteOrder(UUID id) {

        try {
            orderRepo.deleteById(id);
        } catch (Exception e){
            return false;
        }
        return true;
    }

    @Override
    public Order getOrder(UUID id) {
        return orderRepo.findById(id).orElse(null);
    }


}
