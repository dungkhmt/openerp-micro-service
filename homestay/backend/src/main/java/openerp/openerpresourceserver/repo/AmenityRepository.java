package openerp.openerpresourceserver.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import openerp.openerpresourceserver.entity.Amenity;

@Repository
public interface AmenityRepository extends JpaRepository<Amenity, Long>{
    List<Amenity> findByNameContainingIgnoreCase(String name);
}
