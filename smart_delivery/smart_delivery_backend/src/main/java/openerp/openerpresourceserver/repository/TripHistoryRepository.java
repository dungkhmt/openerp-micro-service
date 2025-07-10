package openerp.openerpresourceserver.repository;

import openerp.openerpresourceserver.entity.TripHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TripHistoryRepository extends JpaRepository<TripHistory, UUID> {
    List<TripHistory> findByTripIdOrderByCreatedAtAsc(UUID tripId);
}
