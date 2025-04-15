package openerp.openerpresourceserver.service;

import java.util.List;

import openerp.openerpresourceserver.entity.Payment;

public interface PaymentService {
    Payment getPaymentById(Long paymentId);

    List<Payment> getPaymentsByBooking(Long bookingId);

    List<Payment> getPaymentsByStatus(String status);

    Payment createPayment(Payment payment);

    Payment updatePaymentStatus(Long paymentId, String status);
    
}
