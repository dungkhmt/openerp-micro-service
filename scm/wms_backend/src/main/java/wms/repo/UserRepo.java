package wms.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wms.entity.DeliveryTrip;
import wms.entity.UserLogin;
import wms.entity.UserRegister;

public interface UserRepo extends JpaRepository<UserRegister, Long> {
    @Query(value =
                    """
                            select *
                            from user_register ur
                            where ur.registered_roles like '%SCM%'
                            and ur.user_login_id = :id
                    """
            , nativeQuery = true)
    UserRegister getUserByUserLoginId(String id);

    @Query(value = """
            select *
            from user_register ur
            where ur.registered_roles like concat('%', :role, '%')
            """, nativeQuery = true)
    Page<UserRegister> search(Pageable pageable, String role);
}
