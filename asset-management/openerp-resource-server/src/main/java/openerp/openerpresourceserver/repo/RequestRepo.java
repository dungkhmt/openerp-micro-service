package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RequestRepo extends JpaRepository<Request, Integer> {
    @Query(value = "SELECT * FROM asset_management_request WHERE user_id := userId", nativeQuery = true)
    List<Request> findByUserId(String userId);
}
