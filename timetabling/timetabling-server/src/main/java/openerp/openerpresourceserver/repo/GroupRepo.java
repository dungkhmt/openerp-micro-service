package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.model.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepo extends JpaRepository<Group, Long> {
    @Query(value = "SELECT DISTINCT semester FROM public.timetabling_group", nativeQuery = true)
    List<String> getSemester();

    Optional<Group> findByGroupName(String groupName);

    List<Group> getAllByGroupName(String groupName);

    void deleteById(Long id);
}
