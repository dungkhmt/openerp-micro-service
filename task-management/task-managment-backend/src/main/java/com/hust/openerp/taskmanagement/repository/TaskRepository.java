package com.hust.openerp.taskmanagement.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hust.openerp.taskmanagement.dto.SearchTaskDTO;
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
}
