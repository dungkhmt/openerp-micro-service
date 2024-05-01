package openerp.openerpresourceserver.generaltimetabling.repo;


import openerp.openerpresourceserver.generaltimetabling.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, String> {

}
