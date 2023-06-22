package openerp.containertransport.repo;

import openerp.containertransport.entity.Trailer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrailerRepo extends JpaRepository<Trailer, Long> {
    Trailer findById(long id);
    Trailer findByUid(String uid);
}
