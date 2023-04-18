package com.hust.baseweb.applications.taskmanagement.repository;

import com.hust.baseweb.applications.taskmanagement.entity.Task;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID>, CrudRepository<Task, UUID> {

    @Query(value = "SELECT e.* FROM backlog_task e WHERE e.backlog_project_id = :projectId ORDER BY e.created_stamp DESC",
           nativeQuery = true)
    List<Task> findAllTasksByProjectId(@Param("projectId") UUID projectId);

    Task save(Task task);

    @Query(value =
               "Select c.backlog_task_category_id, c.backlog_task_category_name, count(e.backlog_task_id)\\:\\:int \n" +
               "from backlog_task_category c \n" +
               "left join backlog_task e on e.backlog_task_category_id = c.backlog_task_category_id\n" +
               "where e.backlog_project_id = :projectId or e.backlog_project_id is null \n" +
               "group by c.backlog_task_category_id",
           nativeQuery = true)
    List<Object[]> getTaskStaticsCategoryInProject(@Param("projectId") UUID projectId);

    @Query(value = "Select s.status_id, s.description, count(b.backlog_task_id)\\:\\:int\n" +
                   "from status_item s \n" +
                   "left join backlog_task b on b.status_id = s.status_id\n" +
                   "where s.status_type_id = 'BACKLOG_STATUS' \n" +
                   "AND ( b.backlog_project_id = :projectId OR b.backlog_project_id is null)\n" +
                   "group by s.status_id\n", nativeQuery = true)
    List<Object[]> getTaskStaticsStatusInProject(@Param("projectId") UUID projectId);

    @Query(value = "Select e.* from backlog_task e left join backlog_task_assignable b on e.backlog_task_id = b.backlog_task_id where e.backlog_project_id = :projectId and b.assigned_to_party_id = CAST(:partyId AS uuid) and (:categoryId = '' or e.backlog_task_category_id = CAST(:categoryId AS varchar)) and (e.status_id = CAST(:statusId AS varchar)) and (:priorityId = '' or e.priority_id = CAST(:priorityId AS varchar)) and (:keyName = '' or e.backlog_task_name like :keyName%)",
           nativeQuery = true)
    List<Task> getAllTaskByFiltersWithPartyId(
        @Param("projectId") UUID projectId,
        @Param("statusId") String statusId,
        @Param("categoryId") String categoryId,
        @Param("partyId") UUID partyId,
        @Param("priorityId") String priorityId,
        @Param("keyName") String keyName
    );

    @Query(value = "Select e.* from backlog_task e left join backlog_task_assignable b on e.backlog_task_id = b.backlog_task_id where e.backlog_project_id = :projectId and b.assigned_to_party_id = CAST(:partyId AS uuid) and (:categoryId = '' or e.backlog_task_category_id = CAST(:categoryId AS varchar)) and (e.status_id = CAST(:statusId AS varchar)) and (:priorityId = '' or e.priority_id = CAST(:priorityId AS varchar)) and (:keyName = '' or e.backlog_task_name like :keyName%) and (e.due_date between :startDate and :endDate)",
           nativeQuery = true)
    List<Task> getAllTaskByFiltersWithPartyIdAndRangeDate(
        @Param("projectId") UUID projectId,
        @Param("statusId") String statusId,
        @Param("categoryId") String categoryId,
        @Param("partyId") UUID partyId,
        @Param("priorityId") String priorityId,
        @Param("keyName") String keyName,
        @Param("startDate") Date startDate,
        @Param("endDate") Date endDate
    );

    @Query(value = "Select e.* from backlog_task e left join backlog_task_assignable b on e.backlog_task_id = b.backlog_task_id where e.backlog_project_id = :projectId and (:categoryId = '' or e.backlog_task_category_id = CAST(:categoryId AS varchar)) and e.status_id = :statusId and (:priorityId = '' or e.priority_id = CAST(:priorityId AS varchar)) and (:keyName = '' or e.backlog_task_name like :keyName%)",
           nativeQuery = true)
    List<Task> getAllTaskByFiltersWithoutPartyId(
        @Param("projectId") UUID projectId,
        @Param("statusId") String statusId,
        @Param("categoryId") String categoryId,
        @Param("priorityId") String priorityId,
        @Param("keyName") String keyName
    );

    @Query(value = "Select e.* from backlog_task e left join backlog_task_assignable b on e.backlog_task_id = b.backlog_task_id where e.backlog_project_id = :projectId and (:categoryId = '' or e.backlog_task_category_id = CAST(:categoryId AS varchar)) and e.status_id = :statusId and (:priorityId = '' or e.priority_id = CAST(:priorityId AS varchar)) and (:keyName = '' or e.backlog_task_name like :keyName%) and (e.due_date between :startDate and :endDate)",
           nativeQuery = true)
    List<Task> getAllTaskByFiltersWithoutPartyIdAndRangeDate(
        @Param("projectId") UUID projectId,
        @Param("statusId") String statusId,
        @Param("categoryId") String categoryId,
        @Param("priorityId") String priorityId,
        @Param("keyName") String keyName,
        @Param("startDate") Date startDate,
        @Param("endDate") Date endDate
    );
}
