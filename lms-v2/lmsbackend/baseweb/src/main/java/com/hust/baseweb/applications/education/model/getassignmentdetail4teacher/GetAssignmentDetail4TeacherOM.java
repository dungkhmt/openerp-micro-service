package com.hust.baseweb.applications.education.model.getassignmentdetail4teacher;

import com.hust.baseweb.applications.education.model.getassignmentdetail.AssignmentDetailOM;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetAssignmentDetail4TeacherOM {

    private AssignmentDetailOM assignmentDetail;

    private List<Submission> submissions;

    private String noSubmissions;
}
