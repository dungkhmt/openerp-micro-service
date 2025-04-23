package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HolidayRepository extends JpaRepository<Holiday, Long>, JpaSpecificationExecutor<Holiday> {
    boolean existsByDateAndStatus(LocalDate date, Integer status);

    boolean existsByIdNotAndDateAndStatus(Long id, LocalDate date, Integer status);

    List<Holiday> findByDateBetweenAndTypeAndStatus(LocalDate startDate, LocalDate endDate, int type, int status);

    Optional<Holiday> findByDateAndTypeAndStatus(LocalDate date, int type, int status);
}
