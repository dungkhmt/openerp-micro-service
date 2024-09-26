package openerp.openerpresourceserver.repo;


import openerp.openerpresourceserver.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepo extends JpaRepository<User, String> {
}
