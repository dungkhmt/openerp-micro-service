package wms.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wms.algorithms.entity.Truck;
import wms.entity.TruckEntity;
import wms.entity.UserLogin;

public interface TruckRepo extends JpaRepository<TruckEntity, Long> {
//    @Query(value = "select st.*, ul.user_login_id from scm_truck st left join user_login ul on st.user_id = ul.user_login_id", nativeQuery = true)
    @Query(value = "select * from scm_truck where is_deleted = 0", nativeQuery = true)
    Page<TruckEntity> search(Pageable pageable);
    @Query(value = "select * from scm_truck where scm_truck.id = :id and is_deleted = 0 limit 1", nativeQuery = true)
    TruckEntity getTruckById(long id);
    TruckEntity getTruckByCode(String code);
    @Query(value = "select scm_truck.user_id from scm_truck where scm_truck.code = :truckCode", nativeQuery = true)
    String getUserFromTruck(String truckCode);

    @Query(value = "select * from scm_truck where scm_truck.user_id = :user_id", nativeQuery = true)
    TruckEntity getTruckFromUser(String user_id);
}
