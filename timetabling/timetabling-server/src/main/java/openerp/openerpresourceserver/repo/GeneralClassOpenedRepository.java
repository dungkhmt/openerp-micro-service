package openerp.openerpresourceserver.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import openerp.openerpresourceserver.model.entity.general.GeneralClassOpened;

@Repository
public interface GeneralClassOpenedRepository extends JpaRepository<GeneralClassOpened, Long> {

    List<GeneralClassOpened> findAllBySemester(String semester);
    
}
