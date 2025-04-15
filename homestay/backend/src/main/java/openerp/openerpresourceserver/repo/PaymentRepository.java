package openerp.openerpresourceserver.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import openerp.openerpresourceserver.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByBookingId(Long bookingId);

    List<Payment> findByPaymentStatus(String paymentStatus);
}
