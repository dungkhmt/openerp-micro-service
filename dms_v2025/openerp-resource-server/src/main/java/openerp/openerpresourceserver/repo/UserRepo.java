package openerp.openerpresourceserver.repo;


import openerp.openerpresourceserver.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

}
