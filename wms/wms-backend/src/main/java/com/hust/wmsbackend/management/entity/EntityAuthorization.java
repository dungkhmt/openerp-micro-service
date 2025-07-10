package com.hust.wmsbackend.management.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

/**
 * Contains information about menu and screen permissions
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
// no se lay danh sach cac menu o bang entity_authorization nay
public class EntityAuthorization {

    @Id
    private String id;

    // User's role
    private String roleId;

    private String description;

    @LastModifiedDate
    private Date lastUpdated;

    @CreatedDate
    private Date created;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
