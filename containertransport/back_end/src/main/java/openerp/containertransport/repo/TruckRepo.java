package openerp.containertransport.repo;

import openerp.containertransport.entity.Truck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TruckRepo extends JpaRepository<Truck, Long> {
    Truck findByUid(String uid);
}
