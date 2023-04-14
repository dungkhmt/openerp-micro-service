package openerp.containertransport.repo;

import openerp.containertransport.entity.TripItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripItemRepo extends JpaRepository<TripItem, Long> {
}
