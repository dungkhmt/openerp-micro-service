package com.hust.baseweb.applications.taskmanagement.entity;

import com.hust.baseweb.entity.Party;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMemberId implements Serializable {

    @Column(name = "backlog_project_id")
    private UUID projectId;

    @Column(name = "member_party_id")
    private UUID partyID;
}
