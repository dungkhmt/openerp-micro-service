package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RequestRepo extends JpaRepository<Request, Integer> {
    @Query(value = "SELECT * FROM asset_management_request ORDER BY since DESC", nativeQuery = true)
    List<Request> getAllByLastUpdated();

    @Query(value = "SELECT * FROM asset_management_request WHERE user_id = :creatorId", nativeQuery = true)
    List<Request> findByCreatorId(String creatorId);

    @Query(value = "SELECT * FROM asset_management_request WHERE admin_id = :adminId", nativeQuery = true)
    List<Request> findByAdminId(String adminId);
}
