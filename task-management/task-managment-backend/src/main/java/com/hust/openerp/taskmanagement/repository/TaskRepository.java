package com.hust.openerp.taskmanagement.repository;

import com.hust.openerp.taskmanagement.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID>, JpaSpecificationExecutor<Task> {

    @Query(value = "SELECT e.* FROM task_management_task e WHERE e.project_id = :projectId ORDER BY e.created_stamp DESC", nativeQuery = true)
    List<Task> findAllTasksByProjectId(@Param("projectId") UUID projectId);

    @Query(value = "Select c.category_id, c.category_name, count(e.id)\\:\\:int \n"
            +
            "from task_management_task_category c \n" +
            "left join task_management_task e on e.category_id = c.category_id\n" +
            "where e.project_id = :projectId or e.project_id is null \n" +
            "group by c.category_id", nativeQuery = true)
    List<Object[]> getTaskStaticsCategoryInProject(@Param("projectId") UUID projectId);

    @Query(value = "Select s.status_id, s.description, count(b.id)\\:\\:int\n" +
            "from status_item s \n" +
            "left join task_management_task b on b.status_id = s.status_id\n" +
            "where s.status_type_id = 'BACKLOG_STATUS' \n" +
            "AND ( b.project_id = :projectId OR b.project_id is null)\n" +
            "group by s.status_id\n", nativeQuery = true)
    List<Object[]> getTaskStaticsStatusInProject(@Param("projectId") UUID projectId);

    @Query(value = "Select e.* from task_management_task e left join task_management_task_assignment b on e.id = b.task_id where e.project_id = :projectId and b.assigned_to_user_id = :userId and (:categoryId = '' or e.category_id = CAST(:categoryId AS varchar)) and (e.status_id = CAST(:statusId AS varchar)) and (:priorityId = '' or e.priority_id = CAST(:priorityId AS varchar)) and (:keyName = '' or e.task_name like :keyName%)", nativeQuery = true)
    List<Task> getAllTaskByFiltersWithPartyId(
            @Param("projectId") UUID projectId,
            @Param("statusId") String statusId,
            @Param("categoryId") String categoryId,
            @Param("userId") String userId,
            @Param("priorityId") String priorityId,
            @Param("keyName") String keyName);

    @Query(value = "Select e.* from task_management_task e left join task_management_task_assignment b on e.id = b.task_id where e.project_id = :projectId and b.assigned_to_user_id = :userId and (:categoryId = '' or e.category_id = CAST(:categoryId AS varchar)) and (e.status_id = CAST(:statusId AS varchar)) and (:priorityId = '' or e.priority_id = CAST(:priorityId AS varchar)) and (:keyName = '' or e.task_name like :keyName%) and (e.due_date between :startDate and :endDate)", nativeQuery = true)
    List<Task> getAllTaskByFiltersWithPartyIdAndRangeDate(
            @Param("projectId") UUID projectId,
            @Param("statusId") String statusId,
            @Param("categoryId") String categoryId,
            @Param("userId") String userId,
            @Param("priorityId") String priorityId,
            @Param("keyName") String keyName,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);

    @Query(value = "Select e.* from task_management_task e left join task_management_task_assignment b on e.id = b.task_id where e.project_id = :projectId and (:categoryId = '' or e.category_id = CAST(:categoryId AS varchar)) and e.status_id = :statusId and (:priorityId = '' or e.priority_id = CAST(:priorityId AS varchar)) and (:keyName = '' or e.task_name like :keyName%)", nativeQuery = true)
    List<Task> getAllTaskByFiltersWithoutPartyId(
            @Param("projectId") UUID projectId,
            @Param("statusId") String statusId,
            @Param("categoryId") String categoryId,
            @Param("priorityId") String priorityId,
            @Param("keyName") String keyName);

    @Query(value = "Select e.* from task_management_task e left join task_management_task_assignment b on e.id = b.task_id where e.project_id = :projectId and (:categoryId = '' or e.category_id = CAST(:categoryId AS varchar)) and e.status_id = :statusId and (:priorityId = '' or e.priority_id = CAST(:priorityId AS varchar)) and (:keyName = '' or e.task_name like :keyName%) and (e.due_date between :startDate and :endDate)", nativeQuery = true)
    List<Task> getAllTaskByFiltersWithoutPartyIdAndRangeDate(
            @Param("projectId") UUID projectId,
            @Param("statusId") String statusId,
            @Param("categoryId") String categoryId,
            @Param("priorityId") String priorityId,
            @Param("keyName") String keyName,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);
}
