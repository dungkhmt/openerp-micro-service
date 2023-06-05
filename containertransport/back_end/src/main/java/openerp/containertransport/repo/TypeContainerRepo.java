package openerp.containertransport.repo;

import openerp.containertransport.entity.TypeContainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeContainerRepo extends JpaRepository<TypeContainer, Long> {
    TypeContainer findByTypeContainerCode (String typeContainerCode);
}
