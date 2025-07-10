package openerp.openerpresourceserver.repository;

import openerp.openerpresourceserver.dto.TodayAssignmentDto;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public interface AssignOrderCollectorRepositoryCustom {
    List<TodayAssignmentDto> findByHubIdAndCreatedAtBetween(
            @Param("hubId") UUID hubId,
            @Param("startDate") Timestamp startDate,
            @Param("endDate") Timestamp endDate
    );
}
