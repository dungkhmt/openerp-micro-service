package openerp.openerpresourceserver.trainingprogcourse.repository;

import openerp.openerpresourceserver.trainingprogcourse.enity.TrainingProgProgram;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainingProgProgramRepo  extends JpaRepository<TrainingProgProgram, String> {

    @Query("SELECT p FROM TrainingProgProgram p WHERE (:keyword IS NULL OR :keyword = '' OR p.name LIKE %:keyword%)" +
            "OR p.id LIKE %:keyword%")
    Page<TrainingProgProgram> findByKeyword(String keyword, Pageable pageable);
}
