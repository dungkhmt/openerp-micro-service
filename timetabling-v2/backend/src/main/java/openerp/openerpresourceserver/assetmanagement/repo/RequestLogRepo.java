package openerp.openerpresourceserver.assetmanagement.repo;

import openerp.openerpresourceserver.assetmanagement.entity.RequestLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RequestLogRepo extends JpaRepository<RequestLog, Integer> {
    @Query(value = "SELECT * FROM asset_management_request_log WHERE user_id = :userId", nativeQuery = true)
    List<RequestLog> getByUserId(String userId);
}

