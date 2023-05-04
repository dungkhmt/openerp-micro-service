package wms.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wms.entity.DeliveryTrip;

import java.util.List;

public interface DeliveryTripRepo extends JpaRepository<DeliveryTrip, Long> {
    @Query(value = "select * from scm_delivery_trip", nativeQuery = true)
    Page<DeliveryTrip> search(Pageable pageable);
    DeliveryTrip getDeliveryTripById(long id);
    DeliveryTrip getDeliveryTripByCode(String code);

    // https://stackoverflow.com/questions/42145271/noviablealtexception-while-using-spring-data-query-with-hibernate
    @Query(value = "select * from scm_delivery_trip sdt where sdt.user_in_charge = :staffCode", nativeQuery = true)
    List<DeliveryTrip> getDeliveryTripsByStaff(String staffCode);

    @Query(value = "select * from scm_delivery_trip sdt where sdt.facility_code = :facilityCode", nativeQuery = true)
    List<DeliveryTrip> getDeliveryTripsByFacility(String facilityCode);
}
