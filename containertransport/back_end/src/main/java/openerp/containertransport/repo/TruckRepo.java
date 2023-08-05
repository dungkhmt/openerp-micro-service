package openerp.containertransport.repo;

import openerp.containertransport.entity.Truck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TruckRepo extends JpaRepository<Truck, Long> {
    Truck findByUid(String uid);

    @Query(value = "SELECT COUNT(container_transport_trucks.uid) FROM container_transport_trucks WHERE status = :status", nativeQuery = true)
    float countTruckByStatus(String status);
}
