package openerp.coderunnerservice.repo;


import openerp.coderunnerservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, String> {

}
