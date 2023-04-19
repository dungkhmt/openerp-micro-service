package wms.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import wms.entity.ProductEntity;
import wms.entity.UserLogin;

public interface UserRepo extends JpaRepository<UserLogin, Long> {
//    UserLogin getUserByUserLoginId(String id);
}
