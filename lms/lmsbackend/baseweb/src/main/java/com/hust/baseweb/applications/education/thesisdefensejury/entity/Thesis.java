package com.hust.baseweb.applications.education.thesisdefensejury.entity;

import com.hust.baseweb.applications.education.entity.mongodb.Teacher;
import com.hust.baseweb.applications.education.teacherclassassignment.entity.EduTeacher;
import com.hust.baseweb.applications.education.teacherclassassignment.entity.TeacherCourse;
import com.hust.baseweb.entity.UserLogin;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "thesis") // Entity map voi bang thesis
@NoArgsConstructor
public class Thesis {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "thesis_name")
    private String thesisName;

    @Column(name = "thesis_abstract")
    private String thesisAbstract;

//    @ManyToOne
    @Column(name = "program_id") // thông qua khóa ngoại program_id
    private UUID programId;

    @Column(name = "thesis_defense_plan_id") // thông qua khóa ngoại thesis_defense_plan_id
    private String defensePlanId;

    @Column(name = "student_name")
    private String studentName;
//

    @Column(name = "supervisor_id") // thông qua khóa ngoại supervisor_id
    private String supervisor;
//

    @Column(name = "created_by_user_login_id") // thông qua khóa ngoại created_by_user_login_id
    private String userLogin;
//

    @Column(name = "scheduled_reviewer_id") // thông qua khóa ngoại scheduled_reviewer_id
    private String scheduled_reviewer_id;
//
//    @ManyToOne
    @Column(name = "scheduled_jury_id", nullable = true) // thông qua khóa ngoại scheduled_jury_id
    private UUID defenseJury;
//
//    @ManyToOne
    @Column(name = "keywords") // thông qua khóa ngoại keyword
    private String thesisKeyword;

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

    public String getThesisName() {
        return thesisName;
    }

    public void setThesisName(String thesisName) {
        this.thesisName = thesisName;
    }

    public String getThesisAbstract() {
        return thesisAbstract;
    }

    public void setThesisAbstract(String thesisAbstract) {
        this.thesisAbstract = thesisAbstract;
    }

    public UUID getProgramId() {
        return programId;
    }

    public void setProgramId(UUID programId) {
        this.programId = programId;
    }

    public String getDefensePlanId() {
        return defensePlanId;
    }

    public void setDefensePlanId(String defensePlanId) {
        this.defensePlanId = defensePlanId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(String supervisor) {
        this.supervisor = supervisor;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public String getScheduled_reviewer_id() {
        return scheduled_reviewer_id;
    }

    public void setScheduled_reviewer_id(String scheduled_reviewer_id) {
        this.scheduled_reviewer_id = scheduled_reviewer_id;
    }

    public UUID getDefenseJury() {
        return defenseJury;
    }

    public void setDefenseJury(UUID defenseJury) {
        this.defenseJury = defenseJury;
    }

    public String getThesisKeyword() {
        return thesisKeyword;
    }

    public void setThesisKeyword(String thesisKeyword) {
        this.thesisKeyword = thesisKeyword;
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
}
