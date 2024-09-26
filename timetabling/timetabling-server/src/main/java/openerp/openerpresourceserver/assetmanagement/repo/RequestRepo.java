package openerp.openerpresourceserver.assetmanagement.repo;

import openerp.openerpresourceserver.assetmanagement.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RequestRepo extends JpaRepository<Request, Integer> {
    @Query(value = "SELECT * FROM asset_management_request ORDER BY since DESC", nativeQuery = true)
    List<Request> getAllByLastUpdated();

    @Query(value = "SELECT * FROM asset_management_request WHERE user_id = :creatorId", nativeQuery = true)
    List<Request> findByCreatorId(String creatorId);

    @Query(value = "SELECT * FROM asset_management_request WHERE admin_id = :adminId", nativeQuery = true)
    List<Request> findByAdminId(String adminId);

    @Query(value = "SELECT user_id, COUNT(*) AS request_count FROM asset_management_request GROUP BY user_id ORDER BY request_count DESC LIMIT 5", nativeQuery = true)
    List<String> getTopUsers();
}
