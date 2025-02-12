package openerp.openerpresourceserver.repository;


import openerp.openerpresourceserver.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, String> {

}
