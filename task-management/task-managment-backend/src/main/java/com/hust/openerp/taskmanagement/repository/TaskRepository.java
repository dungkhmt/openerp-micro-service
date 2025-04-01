package com.hust.openerp.taskmanagement.repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hust.openerp.taskmanagement.dto.SearchTaskDTO;
import com.hust.openerp.taskmanagement.dto.TaskStatisticByStatusDTO;
import com.hust.openerp.taskmanagement.entity.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID>, JpaSpecificationExecutor<Task> {

    @Modifying
    @Query(value = """
            UPDATE Task t SET t.rgt = t.rgt + 2 WHERE t.rgt >= :parentRgt AND t.ancestorId = :ancestorId
            """)
    void updateNestedSetsRgt(@Param("parentRgt") Integer parentRgt, @Param("ancestorId") UUID ancestorId);

    @Modifying
    @Query(value = """
            UPDATE Task t SET t.lft = t.lft + 2 WHERE t.lft > :parentRgt AND t.ancestorId = :ancestorId
            """)
    void updateNestedSetsLft(@Param("parentRgt") Integer parentRgt, @Param("ancestorId") UUID ancestorId);

    @Query("SELECT t FROM Task t WHERE t.ancestorId = :ancestorId AND t.lft >= :lft AND t.rgt <= :rgt ORDER BY t.lft")
    List<Task> getTaskByAncestorIdAndLftAndRgt(@Param("ancestorId") UUID ancestorId, @Param("lft") Integer lft,
            @Param("rgt") Integer rgt);

    @Query("""
            select new com.hust.openerp.taskmanagement.dto.SearchTaskDTO(t.id, t.name, t.projectId) from Task t
            join (
                select tl.taskId as taskId, MAX(tl.createdAt) as lastActivity from TaskLog tl
                where tl.creatorId = ?1
                group by tl.taskId
                order by lastActivity desc
                limit ?2
            ) as recentTasks on t.id = recentTasks.taskId
            order by recentTasks.lastActivity desc
            """)
    List<SearchTaskDTO> getRecentTask(String userId, Integer limit);

    @Query("""
            select new com.hust.openerp.taskmanagement.dto.SearchTaskDTO(t.id, t.name, t.projectId) from Task t
            where t.name ilike %:keyword%
            and t.projectId in (
                    select pm.projectId from ProjectMember pm where pm.userId = :userId
                )
            """)
    List<SearchTaskDTO> search(String userId, String keyword);

    @Query("""
            Select
                count(t.id) as totalCount,
                count(case when t.statusId = :status
                           and t.lastUpdatedStamp between :startDate and :endDate
                           then 1 end) as count
            from Task t
            where t.projectId = :projectId
            """)
    TaskStatistic getCountAndTotalCountByStatus(UUID projectId, Date startDate, Date endDate, String status);

    @Query("""
            Select count(t.id) as totalCount, count(case when t.createdStamp between :startDate and :endDate then 1 end) as count
            from Task t
            where t.projectId = :projectId
            """)
    TaskStatistic getCountAndTotalCountOfAllStatus(UUID projectId, Date startDate, Date endDate);

    @Query("""
            Select new com.hust.openerp.taskmanagement.dto.TaskStatisticByStatusDTO(
                si.statusId,
                count(t.statusId)
            ) from Task t
            right join Status si on t.statusId = si.statusId and (
                        ( t.projectId = :projectId and
                          t.lastUpdatedStamp between :startDate and :endDate )
                        or t.projectId is null
                    )
            where si.type = 'TASK_STATUS'
            group by si.statusId
            """)
    List<TaskStatisticByStatusDTO> getTaskStatisticWorkloadByStatus(UUID projectId, Date startDate, Date endDate);

    @Query(value = """
            select * from task_management_task t
            where t.assignee_id is not null
            and t.due_date between CURRENT_DATE AND DATE_TRUNC('day', CURRENT_DATE + INTERVAL '?1 days') + INTERVAL '1 day' - INTERVAL '1 second'""", nativeQuery = true)
    List<Task> getTasksDueDateIntervalDay(Integer intervalDay);
    
    @Modifying
    @Query("UPDATE Task t SET t.assigneeId = NULL WHERE t.assigneeId = :memberId AND t.projectId = :projectId")
    void unassignUserFromTasks(@Param("memberId") String memberId, @Param("projectId") UUID projectId);
    
    @Modifying
    @Query("UPDATE Task t SET t.priorityId = 'NORMAL' WHERE t.priorityId = :priorityId")
    void updatePriorityToDefault(@Param("priorityId") String priorityId);
    
    @Modifying
    @Query("UPDATE Task t SET t.categoryId = 'REQUEST' WHERE t.categoryId = :categoryId")
    void updateCategoryToDefault(@Param("categoryId") String categoryId);
    
    @Modifying
    @Query("UPDATE Task t SET t.statusId = 'TASK_OPEN' WHERE t.statusId = :statusId")
    void updateStatusToDefault(@Param("statusId") String statusId);

    
    List<Task> findByEventId(UUID eventId);
    
    List<Task> findByProjectIdAndEventIdIsNull(UUID projectId);

    public static interface TaskStatistic {
        Long getTotalCount();

        Long getCount();
    }

}
