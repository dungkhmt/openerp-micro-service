package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.model.entity.ClassOpened;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassOpenedRepo extends JpaRepository<ClassOpened, Long> {

    void deleteById(Long id);

    List<ClassOpened> findAll();

    @Query(value = "SELECT t FROM public.timetabling_class_opened t WHERE t.class_opened_id IN :ids", nativeQuery = true)
    List<ClassOpened> getAllByIds(@Param("ids") List<Long> ids);
}
