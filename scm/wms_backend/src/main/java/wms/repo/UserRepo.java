package wms.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wms.entity.DeliveryTrip;
import wms.entity.UserLogin;

public interface UserRepo extends JpaRepository<UserLogin, Long> {
    @Query(value = "select * from user_login where user_login_id = :id", nativeQuery = true)
    UserLogin getUserByUserLoginId(String id);

    @Query(value = "select * from user_login", nativeQuery = true)
    Page<UserLogin> search(Pageable pageable);
}
