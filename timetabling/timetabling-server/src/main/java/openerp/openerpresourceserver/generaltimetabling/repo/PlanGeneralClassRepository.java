package openerp.openerpresourceserver.generaltimetabling.repo;

import openerp.openerpresourceserver.generaltimetabling.model.entity.general.PlanGeneralClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanGeneralClassRepository extends JpaRepository<PlanGeneralClass, Long> {
    List<PlanGeneralClass> findAllBySemester(String semester);

    void deleteAllBySemester(String semester);
}
