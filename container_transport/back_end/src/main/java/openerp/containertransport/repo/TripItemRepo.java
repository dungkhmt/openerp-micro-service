package openerp.containertransport.repo;

import openerp.containertransport.entity.TripItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripItemRepo extends JpaRepository<TripItem, Long> {
    List<TripItem> findByTripId(long id);
}
