package com.hust.openerp.taskmanagement.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@IdClass(UserSkill.UserSkillId.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "task_management_user_skill")
public class UserSkill {
	@Id
    @Column(name = "user_id")
    private String userId;

	@Id
    @Column(name = "skill_id")
    private String skillId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, insertable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "skill_id", nullable = false, insertable = false, updatable = false)
    private Skill skill;


    @SuppressWarnings("serial")
	@AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class UserSkillId implements Serializable {
        private String userId;
        private String skillId;

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof UserSkillId))
                return false;
            UserSkillId that = (UserSkillId) o;
            return userId.equals(that.userId) && skillId.equals(that.skillId);
        }

        @Override
        public int hashCode() {
            return userId.hashCode() + skillId.hashCode();
        }
    }
}
