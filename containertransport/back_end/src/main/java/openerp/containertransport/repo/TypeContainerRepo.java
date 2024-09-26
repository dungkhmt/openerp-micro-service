package openerp.containertransport.repo;

import openerp.containertransport.entity.TypeContainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeContainerRepo extends JpaRepository<TypeContainer, Long> {
    TypeContainer findByTypeContainerCode (String typeContainerCode);

    @Query(value = "SELECT container_transport_type_container.total FROM container_transport_type_container where size = :size", nativeQuery = true)
    Float countContainer(Integer size);
}
