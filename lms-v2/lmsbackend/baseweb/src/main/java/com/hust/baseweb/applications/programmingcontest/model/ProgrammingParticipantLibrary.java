package com.hust.baseweb.applications.programmingcontest.entity;

import com.hust.baseweb.entity.UserLogin;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Data
@Entity
@Table(name = "programming_participant_library")
public class ProgrammingParticipantLibrary {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "uuid2")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "user_id", length = 60, nullable = false)
    private String userId;

    @Column(name = "name", length = 200, nullable = false)
    private String name;

    @Column(name = "language", length = 200, nullable = false)
    private String language;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "status", length = 100)
    private String status;

    @Column(name = "last_updated_stamp", columnDefinition = "timestamp default current_date")
    private Date lastUpdatedStamp;

    @Column(name = "created_stamp", columnDefinition = "timestamp default current_date")
    private Date createdStamp;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_login_id", insertable = false, updatable = false)
    private UserLogin userLogin;
}
