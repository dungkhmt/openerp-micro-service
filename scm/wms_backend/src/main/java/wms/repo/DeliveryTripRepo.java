package wms.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wms.entity.DeliveryTrip;

public interface DeliveryTripRepo extends JpaRepository<DeliveryTrip, Long> {
    @Query(value = "select * from scm_delivery_trip", nativeQuery = true)
    Page<DeliveryTrip> search(Pageable pageable);
    DeliveryTrip getDeliveryTripById(long id);
    DeliveryTrip getDeliveryTripByCode(String code);
}
