package com.hust.baseweb.applications.whiteboard.entity;

import com.hust.baseweb.applications.education.entity.QuizCourseTopic;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "whiteboard_data")
public class WhiteBoardData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "type")
    private String type;

    @Column(name = "data")
    private String data;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "whiteboard_id", referencedColumnName = "id")
    private Whiteboard whiteboard;

    @CreatedDate
    private Date createdDate;

    @CreatedBy
    private String createdBy;

    @LastModifiedDate
    private Date lastModifiedDate;

    @LastModifiedBy
    private String lastModifiedBy;
}
