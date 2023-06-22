package openerp.containertransport.repo;

import openerp.containertransport.entity.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacilityRepo extends JpaRepository<Facility, Long> {
    Facility findById(long id);
    Facility findByUid(String uid);
}
