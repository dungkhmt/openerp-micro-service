package openerp.openerpresourceserver.generaltimetabling.repo;

import openerp.openerpresourceserver.generaltimetabling.model.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StateRepo extends JpaRepository<State, Long> {
    @Query(value = "SELECT DISTINCT state FROM public.timetabling_schedule", nativeQuery = true)
    List<String> getState();
}
