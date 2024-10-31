package openerp.openerpresourceserver.log.repo;

import openerp.openerpresourceserver.log.entity.LmsLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface LmsLogRepo extends JpaRepository<LmsLog, UUID> {
    List<LmsLog> findAllByCreatedStampBetween(Date s, Date e);

    @Query(value="select * from lms_log order by created_stamp desc limit ?1",nativeQuery = true)
    List<LmsLog> getMostRecentLogs(int size);
    //@Query(value = "select count(*) from lms_log", nativeQuery = true)
    //long count();
}
