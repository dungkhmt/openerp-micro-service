package openerp.openerpresourceserver.generaltimetabling.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import openerp.openerpresourceserver.generaltimetabling.model.entity.general.GeneralClass;

@Repository
public interface GeneralClassRepository extends JpaRepository<GeneralClass, Long> {

    void deleteBySemester(String semester);

    List<GeneralClass> findAllBySemester(String semester);

    List<GeneralClass> findAllBySemesterAndGroupName(String semester, String groupName);

    List<GeneralClass> findAllByIdIn(List<Long> l);
}
