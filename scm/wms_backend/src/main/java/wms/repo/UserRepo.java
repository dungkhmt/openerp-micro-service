package wms.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wms.entity.DeliveryTrip;
import wms.entity.UserLogin;
import wms.entity.UserRegister;

import java.util.List;

public interface UserRepo extends JpaRepository<UserRegister, Long> {
    @Query(value = "                            select *\n" +
            "                            from user_register ur\n" +
            "                            where ur.registered_roles like '%SCM%'\n" +
            "                            and ur.user_login_id = :id"
            , nativeQuery = true)
    UserRegister getUserByUserLoginId(String id);

    @Query(value = " select * from user_register ur where ur.registered_roles like concat('%', :role, '%')\n" +
            " and (ur.user_login_id ilike concat('%', :textSearch, '%')\n" +
            "                    or ur.email ilike concat('%', :textSearch, '%')\n" +
            "                    or concat(ur.first_name, ' ', ur.middle_name, ' ', ur.last_name) ilike concat('%', :textSearch, '%')\n" +
            "                    or ur.status_id ilike concat('%', :textSearch, '%'))", nativeQuery = true)
    Page<UserRegister> search(Pageable pageable, String role, String textSearch);

    @Query(value = "select * from scm_user_register where registered_roles like concat('%', :roleName, '%')"
            , nativeQuery = true)
    List<UserRegister> getUsersByRole(String roleName);
}
