package openerp.containertransport.repo;

import openerp.containertransport.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TripRepo extends JpaRepository<Trip, Long> {
    Trip findByUid(String uid);
}
