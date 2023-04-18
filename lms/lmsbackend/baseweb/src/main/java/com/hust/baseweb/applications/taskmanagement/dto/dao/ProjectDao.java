package com.hust.baseweb.applications.taskmanagement.dto.dao;

import com.hust.baseweb.applications.taskmanagement.entity.Project;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class ProjectDao {

    private UUID id;
    private String code;
    private String name;
    private Date lastUpdatedStamp;
    private Date createdStamp;

    public ProjectDao(Project project) {
        this.setId(project.getId());
        this.setCode(project.getCode());
        this.setName(project.getName() != null ? project.getName() : "Dự án chưa được đặt tên");
        this.setLastUpdatedStamp(project.getLastUpdatedStamp());
        this.setCreatedStamp(project.getCreatedStamp());
    }
}
