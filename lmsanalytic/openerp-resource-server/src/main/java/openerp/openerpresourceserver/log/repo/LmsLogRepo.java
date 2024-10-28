package openerp.openerpresourceserver.log.repo;

import openerp.openerpresourceserver.log.entity.LmsLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface LmsLogRepo extends JpaRepository<LmsLog, UUID> {
    List<LmsLog> findAllByCreatedStampBetween(Date s, Date e);
    //@Query(value = "select count(*) from lms_log", nativeQuery = true)
    //long count();
}
