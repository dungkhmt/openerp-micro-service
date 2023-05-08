package com.hust.baseweb.applications.education.classmanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="edu_class_session")
public class EduClassSession {
    public static final String STATUS_CREATED = "CREATED";
    public static final String STATUS_DISABLED = "DISABLED";
    public static final String STATUS_OPEN = "OPEN";
    public static final String STATUS_HIDDEN = "HIDDEN";

    @Id
    @Column(name="session_id")
    @GeneratedValue(strategy=GenerationType.AUTO)
    private UUID sessionId;

    @Column(name="session_name")
    private String sessionName;

    @Column(name="class_id")
    private UUID classId;

    @Column(name="start_datetime")
    private Date startDatetime;

    @Column(name="created_by_user_login_id")
    private String createdByUserLoginId;

    @Column(name="status_id")
    private String statusId;

    @Column(name="description")
    private String description;



}
