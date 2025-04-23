package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.entity.AbsenceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface AbsenceTypeRepository extends JpaRepository<AbsenceType, Long>, JpaSpecificationExecutor<AbsenceType> {
    boolean existsByCodeIgnoreCase(String code);

    boolean existsByIdNotAndCodeIgnoreCase(Long id, String code);

    List<AbsenceType> findByTypeAndStatusOrderById(int type, int status);

    List<AbsenceType> findByStatus(int status);

    List<AbsenceType> findByIdInAndStatus(List<Long> absenceTypeIds, Integer status);

    Optional<AbsenceType> findByCode(String code);
}
