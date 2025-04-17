package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.entity.AttendanceRange;
import openerp.openerpresourceserver.enums.StatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface AttendanceRangeRepository extends JpaRepository<AttendanceRange, Long>,
        JpaSpecificationExecutor<AttendanceRange> {

    boolean existsByCode(String code);

    boolean existsByIdNotAndCode(long id, String code);

    List<AttendanceRange> findAllAndByStatus(StatusEnum status);

    Optional<AttendanceRange> findByCode(String code);

}
