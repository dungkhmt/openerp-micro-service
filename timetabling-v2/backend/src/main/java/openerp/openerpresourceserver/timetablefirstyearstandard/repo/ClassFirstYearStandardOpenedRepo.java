package openerp.openerpresourceserver.timetablefirstyearstandard.repo;

import openerp.openerpresourceserver.timetablefirstyearstandard.entity.ClassFirstYearStandardOpened;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ClassFirstYearStandardOpenedRepo extends JpaRepository<ClassFirstYearStandardOpened, Long> {

}
