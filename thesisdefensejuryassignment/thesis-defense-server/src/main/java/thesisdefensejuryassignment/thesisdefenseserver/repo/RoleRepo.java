package thesisdefensejuryassignment.thesisdefenseserver.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import thesisdefensejuryassignment.thesisdefenseserver.entity.Role;

@Repository

public interface RoleRepo extends JpaRepository<Role, Integer> {
}
