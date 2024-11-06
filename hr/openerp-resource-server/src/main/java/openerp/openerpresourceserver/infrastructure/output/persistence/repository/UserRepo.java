package openerp.openerpresourceserver.infrastructure.output.persistence.repository;


import openerp.openerpresourceserver.infrastructure.output.persistence.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, String> {

}
