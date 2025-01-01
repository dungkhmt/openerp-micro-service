package openerp.openerpresourceserver.repository;


import openerp.openerpresourceserver.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

}
