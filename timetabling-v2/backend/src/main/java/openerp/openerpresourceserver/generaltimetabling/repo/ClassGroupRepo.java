package openerp.openerpresourceserver.generaltimetabling.repo;

import openerp.openerpresourceserver.generaltimetabling.model.entity.ClassGroup;
import openerp.openerpresourceserver.generaltimetabling.model.entity.ClassGroupId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassGroupRepo extends JpaRepository<ClassGroup, ClassGroupId> {

    @Query("SELECT c FROM ClassGroup c WHERE c.classId = :classId")
    List<ClassGroup> findByClassId(@Param("classId") Long classId);

    List<ClassGroup> findAllByClassIdIn(List<Long> classIds);

    Optional<ClassGroup> findByClassIdAndGroupId(Long classId, Long groupId);
}