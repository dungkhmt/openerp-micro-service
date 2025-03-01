package openerp.openerpresourceserver.service.impl;

import jakarta.ws.rs.NotFoundException;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.entity.AssignOrderCollector;
import openerp.openerpresourceserver.entity.Hub;
import openerp.openerpresourceserver.entity.Order;
import openerp.openerpresourceserver.entity.Sender;
import openerp.openerpresourceserver.entity.enumentity.CollectorAssignmentStatus;
import openerp.openerpresourceserver.entity.enumentity.OrderStatus;
import openerp.openerpresourceserver.repository.AssignOrderCollectorRepository;
import openerp.openerpresourceserver.repository.HubRepo;
import openerp.openerpresourceserver.repository.OrderRepo;
import openerp.openerpresourceserver.repository.SenderRepo;
import openerp.openerpresourceserver.service.AssignmentService;
import openerp.openerpresourceserver.utils.DistanceCalculator.HaversineDistanceCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Log4j2
@Service
public class AssignmentServiceImpl implements AssignmentService {

    @Autowired
    private HubRepo hubRepo;
    @Autowired
    private SenderRepo senderRepo;
    @Autowired
    private OrderRepo orderRepo;
    @Autowired
    private AssignOrderCollectorRepository assignOrderCollectorRepository;
    @Override
    public void assignOrderToHub(Order order){
        Sender sender = senderRepo.findById(order.getSenderId()).orElseThrow(() -> new NotFoundException("sender not found"));
        Double x1 = sender.getLatitude();
        Double y1 = sender.getLongitude();

        List<Hub> hubs = hubRepo.findAll();

        Double min = Double.MAX_VALUE;
        Hub assignedHub = null;
        for(Hub hub : hubs){
            // tính khoảng cách kinh độ/vĩ độ trên bản đồ
            Double distance = HaversineDistanceCalculator.calculateDistance(x1,y1, hub.getLatitude(), hub.getLongitude() );

            if (distance < min)
            {
                min = distance;
                assignedHub = hub;
            }
        }
        if(min > 7)
            throw new NotFoundException("Không có hub khả dụng trong phạm vi xung quanh!");
        order.setOriginHubId(assignedHub.getHubId());
        order.setDistance(min);

    }

    public void updateAssignmentStatus(UUID assignmentId, CollectorAssignmentStatus status){
        AssignOrderCollector assignment = assignOrderCollectorRepository.findById(assignmentId).orElseThrow(() -> new NotFoundException("not found assignment"));
        assignment.setStatus(status);
        if(status == CollectorAssignmentStatus.COMPLETED){
            Order order = orderRepo.findById(assignment.getOrderId()).orElseThrow(()-> new NotFoundException("order not found"));
            order.setStatus(OrderStatus.COLLECTED_COLLECTOR);
        }
        assignOrderCollectorRepository.save(assignment);
    }
}
