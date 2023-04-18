package com.hust.baseweb.applications.education.thesisdefensejury.entity;


import com.hust.baseweb.applications.education.teacherclassassignment.entity.EduTeacher;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "teacher_keyword") // Entity map voi bang teacher_thesis_defense_plan
@NoArgsConstructor
public class TeacherKeyword {


    @Column(name = "teacher_id")
    private String teacherId;

    @Id
    @Column(name = "keyword")
    private String keyword;

    @Column(name = "last_updated_stamp")
    private LocalDateTime updatedDateTime;

    @CreationTimestamp
    @Column(name = "created_stamp")
    private LocalDateTime createdTime;

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
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
