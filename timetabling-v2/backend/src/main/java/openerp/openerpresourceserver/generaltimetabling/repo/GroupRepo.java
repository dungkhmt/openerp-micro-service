package openerp.openerpresourceserver.generaltimetabling.repo;

import openerp.openerpresourceserver.generaltimetabling.model.dto.request.GroupDto;
import openerp.openerpresourceserver.generaltimetabling.model.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT new openerp.openerpresourceserver.generaltimetabling.model.dto.request.GroupDto( " +
            "g.id, g.groupName, c.classroom, gp.priority) " +
            "FROM Group g " +
            "LEFT JOIN GroupRoomPriority gp ON g.id = gp.groupId " +
            "LEFT JOIN Classroom c ON gp.roomId = c.id " +
            "WHERE g.id = :groupId")
    List<GroupDto> getGroupWithRoomAndPriorityByGroupId(@Param("groupId") Long groupId);


}
