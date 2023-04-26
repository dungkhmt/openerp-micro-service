package com.hust.baseweb.applications.education.classmanagement.service;

import com.hust.baseweb.applications.education.exception.ResponseFirstType;
import com.hust.baseweb.applications.education.exception.SimpleResponse;
import com.hust.baseweb.applications.education.model.CreateAssignmentIM;
import com.hust.baseweb.applications.education.model.getassignmentdetail.GetAssignmentDetailOM;
import com.hust.baseweb.applications.education.model.getassignmentdetail4teacher.GetAssignmentDetail4TeacherOM;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

public interface AssignmentService {

    String verifyDownloadPermission(UUID assignId, String token);

    GetAssignmentDetailOM getAssignmentDetail(UUID id, String studentId);

    GetAssignmentDetail4TeacherOM getAssignmentDetail4Teacher(UUID assignmentId);

    void downloadSubmmissions(
        String assignmentId,
        List<String> studentIds,
        OutputStream outputStream
    ) throws IOException;

    SimpleResponse deleteAssignment(UUID id);

    ResponseFirstType createAssignment(CreateAssignmentIM im);

    ResponseFirstType updateAssignment(UUID id, CreateAssignmentIM im);

    SimpleResponse saveSubmission(String studentId, UUID assignmentId, MultipartFile file);
}
