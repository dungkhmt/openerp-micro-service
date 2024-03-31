package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.entity.ClassCall;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClassCallRepo extends JpaRepository<ClassCall, Integer> {
    List<ClassCall> findBySemester(String semester, Sort sort);
}
