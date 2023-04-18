package com.hust.baseweb.applications.education.classmanagement.model;

import com.hust.baseweb.applications.education.entity.EduCourse;
import com.hust.baseweb.applications.education.entity.EduDepartment;
import com.hust.baseweb.applications.education.entity.Semester;
import com.hust.baseweb.entity.UserLogin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModelResponseEduClassDetail {
    private UUID id;

    private String classCode;

    private String statusId;

    private String semesterId;

    private String courseId;
    private String courseName;

    private String classType;

    private String createdByUserId;

    private Date createdStamp;

}
