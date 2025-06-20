package com.hust.openerp.taskmanagement.entity;

import java.io.Serializable;
import java.util.UUID;

import com.hust.openerp.taskmanagement.multitenancy.entity.AbstractBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@IdClass(TaskSkill.TaskSkillId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "task_management_task_skill")
public class TaskSkill extends AbstractBaseEntity {

    @Id
    @Column(name = "task_id")
    private UUID taskId;

    @Id
    @Column(name = "skill_id")
    private UUID skillId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false, insertable = false, updatable = false)
    private Task task;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "skill_id", nullable = false, insertable = false, updatable = false)
    private Skill skill;

	@AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class TaskSkillId implements Serializable {
        private UUID taskId;
        private UUID skillId;

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof TaskSkillId))
                return false;
            TaskSkillId that = (TaskSkillId) o;
            return taskId.equals(that.taskId) && skillId.equals(that.skillId);
        }

        @Override
        public int hashCode() {
            return taskId.hashCode() + skillId.hashCode();
        }
    }
}
