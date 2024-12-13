package openerp.openerpresourceserver.thesisdefensejuryassignment.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.DefenseRoom;

@Repository
public interface DefenseRoomRepo extends JpaRepository<DefenseRoom, Integer> {
}
