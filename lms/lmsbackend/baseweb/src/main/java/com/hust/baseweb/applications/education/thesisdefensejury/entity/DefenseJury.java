package com.hust.baseweb.applications.education.thesisdefensejury.entity;


import com.hust.baseweb.entity.UserLogin;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Entity
@Data
@Table(name = "defense_jury") // Entity map voi bang defense_jury
@NoArgsConstructor
public class DefenseJury {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "program_id")
    private UUID programID;

    @Column(name = "thesis_defense_plan_id")
    private String ThesisDefensePlanID;

    @Column(name = "defense_date")
    private LocalDateTime defenseDate;

    @Column(name = "created_by_user_login_id")
    private String userLoginId;

    @Column(name = "max_nbr_thesis")
    private int maxThesis;

    @Column(name = "last_updated_stamp")
    private LocalDateTime updatedDateTime;

    @CreationTimestamp
    @Column(name = "created_stamp")
    private LocalDateTime createdTime;


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getProgramID() {
        return programID;
    }

    public void setProgramID(UUID programID) {
        this.programID = programID;
    }


    public LocalDateTime getDefenseDate() {
        return defenseDate;
    }

    public void setDefenseDate(LocalDateTime defenseDate) {
        this.defenseDate = defenseDate;
    }


    public int getMaxThesis() {
        return maxThesis;
    }

    public void setMaxThesis(int maxThesis) {
        this.maxThesis = maxThesis;
    }

    public LocalDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    public void setUpdatedDateTime(LocalDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public String getThesisDefensePlanID() {
        return ThesisDefensePlanID;
    }

    public void setThesisDefensePlanID(String thesisDefensePlanID) {
        ThesisDefensePlanID = thesisDefensePlanID;
    }

    public String getUserLoginId() {
        return userLoginId;
    }

    public void setUserLoginId(String userLoginId) {
        this.userLoginId = userLoginId;
    }
}
