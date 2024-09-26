package openerp.containertransport.repo;

import openerp.containertransport.entity.Relationship;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RelationshipRepo extends JpaRepository<Relationship, Long> {
}
