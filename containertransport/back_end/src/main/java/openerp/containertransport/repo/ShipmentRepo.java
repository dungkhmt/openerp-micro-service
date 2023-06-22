package openerp.containertransport.repo;

import openerp.containertransport.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipmentRepo extends JpaRepository<Shipment, Long> {
    Shipment findByCode(String code);
    Shipment findByUid(String uid);
}
