package com.hust.baseweb.applications.whiteboard.entity;

import com.hust.baseweb.applications.education.classmanagement.entity.EduClassSession;
import com.hust.baseweb.applications.education.entity.EduCourse;
import com.hust.baseweb.entity.UserLogin;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "whiteboard")
public class Whiteboard {
    @Id
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "data")
    private String data;

    @Column(name = "total_page")
    private Integer totalPage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_session_id", referencedColumnName = "session_id")
    private EduClassSession eduClassSession;

    @CreatedDate
    private Date createdDate;

    @CreatedBy
    private String createdBy;

    @LastModifiedDate
    private Date lastModifiedDate;

    @LastModifiedBy
    private String lastModifiedBy;
}
