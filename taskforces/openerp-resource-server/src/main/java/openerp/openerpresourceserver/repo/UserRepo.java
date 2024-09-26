package openerp.openerpresourceserver.repo;


import openerp.openerpresourceserver.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<UserEntity, String> {

}
