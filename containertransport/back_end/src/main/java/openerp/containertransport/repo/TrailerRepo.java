package openerp.containertransport.repo;

import openerp.containertransport.entity.Trailer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TrailerRepo extends JpaRepository<Trailer, Long> {
    @Query(value = "SELECT * FROM container_transport_trailers WHERE id = :id",
            nativeQuery = true)
    Trailer findByTrailerId(long id);
    Trailer findByUid(String uid);
}
