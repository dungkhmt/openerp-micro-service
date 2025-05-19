package openerp.openerpresourceserver.service.impl;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.entity.AssignOrderShipper;
import openerp.openerpresourceserver.entity.Order;
import openerp.openerpresourceserver.entity.enumentity.OrderStatus;
import openerp.openerpresourceserver.entity.enumentity.ShipperAssignmentStatus;
import openerp.openerpresourceserver.repository.AssignOrderShipperRepository;
import openerp.openerpresourceserver.repository.OrderRepo;
import openerp.openerpresourceserver.service.DeliveryConfirmationService;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeliveryConfirmationServiceImpl implements DeliveryConfirmationService {

    private final OrderRepo orderRepo;
    private final AssignOrderShipperRepository assignOrderShipperRepository;

    // In-memory storage of OTP codes (in a real system, this would be in a database or cache)
    private final Map<UUID, String> otpStorage = new HashMap<>();

    // Random number generator for OTP
    private final SecureRandom random = new SecureRandom();

    @Override
    public String generateConfirmationCode(UUID orderId) {
        // Check if order exists
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderId));

        // Generate a 6-digit OTP
        String otp = String.format("%06d", random.nextInt(1000000));

        // Store the OTP
        otpStorage.put(orderId, otp);

        return otp;
    }

    @Override
    public boolean verifyConfirmationCode(UUID orderId, String code) {
        // Get the stored OTP for this order
        String storedOtp = otpStorage.get(orderId);

        if (storedOtp == null) {
            return false;
        }

        // Check if the provided code matches the stored OTP
        boolean isValid = storedOtp.equals(code);

        // If valid, remove the OTP from storage (one-time use)
        if (isValid) {
            otpStorage.remove(orderId);
        }

        return isValid;
    }

    @Override
    public void storeDeliveryProof(UUID orderId, String proofType, String proofValue) {
        // Get active assignment
        AssignOrderShipper assignment = getActiveAssignment(orderId);



        assignOrderShipperRepository.save(assignment);
    }

    @Override
    @Transactional
    public boolean confirmDelivery(UUID orderId, String confirmationType, String confirmationValue) {
        // Get order
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderId));

        // Check if order is in a valid state for delivery confirmation
        if (order.getStatus() != OrderStatus.ASSIGNED_SHIPPER &&
                order.getStatus() != OrderStatus.SHIPPED) {
            throw new IllegalStateException("Order is not in a valid state for delivery confirmation");
        }

        // Get active assignment
        AssignOrderShipper assignment = getActiveAssignment(orderId);

        // Verify confirmation based on type
        boolean isValid = false;
        if ("otp".equalsIgnoreCase(confirmationType)) {
            isValid = verifyConfirmationCode(orderId, confirmationValue);
        } else if ("signature".equalsIgnoreCase(confirmationType)) {
            // In a real system, we would validate the signature
            isValid = confirmationValue != null && !confirmationValue.isEmpty();
        } else {
            // Default simple validation
            isValid = confirmationValue != null && !confirmationValue.isEmpty();
        }

        if (!isValid) {
            return false;
        }

        // Update assignment
        assignment.setStatus(ShipperAssignmentStatus.COMPLETED);

        assignOrderShipperRepository.save(assignment);

        // Update order status
        order.setStatus(OrderStatus.COMPLETED);
        orderRepo.save(order);

        return true;
    }

    @Override
    @Transactional
    public void registerFailedDeliveryAttempt(UUID orderId, String reason) {
        // Get order
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderId));

        // Get active assignment
        AssignOrderShipper assignment = getActiveAssignment(orderId);

        // Increment delivery attempts counter
        assignment.setDeliveryAttempts(assignment.getDeliveryAttempts() + 1);

        // Update assignment status
        assignment.setStatus(ShipperAssignmentStatus.FAILED_ATTEMPT);

        // Store reason for failure
        assignment.setDeliveryNotes(reason);

        assignOrderShipperRepository.save(assignment);

        // If this is the third attempt, mark as failed and return to hub
        if (assignment.getDeliveryAttempts() >= 3) {
            assignment.setStatus(ShipperAssignmentStatus.RETURNED_TO_HUB);
            assignOrderShipperRepository.save(assignment);

            // Update order status
            order.setStatus(OrderStatus.CANCELLED);
            orderRepo.save(order);
        }
    }

    /**
     * Helper method to get the active assignment for an order
     */
    private AssignOrderShipper getActiveAssignment(UUID orderId) {
        List<AssignOrderShipper> assignments = assignOrderShipperRepository.findByOrderId(orderId);

        return assignments.stream()
                .filter(a -> a.getStatus() != ShipperAssignmentStatus.COMPLETED &&
                        a.getStatus() != ShipperAssignmentStatus.CANCELED)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("No active assignment found for order: " + orderId));
    }
}