package openerp.openerpresourceserver.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import openerp.openerpresourceserver.entity.Homestay;

@Repository
public interface HomestayRepository extends JpaRepository<Homestay, Long>{
    List<Homestay> findByHostId(Long hostId);

    List<Homestay> findByCityIgnoreCase(String city);

    List<Homestay> findByNameContainingIgnoreCase(String name);

    @Query("SELECT h FROM Homestay h WHERE h.deleted_at IS NULL AND " +
            "LOWER(h.city) = LOWER(:city)")
    List<Homestay> findActiveHomestaysByCity(@Param("city") String city);

    @Query("SELECT h FROM Homestay h WHERE h.deleted_at IS NULL")
    List<Homestay> findAllActiveHomestays();
    
}
