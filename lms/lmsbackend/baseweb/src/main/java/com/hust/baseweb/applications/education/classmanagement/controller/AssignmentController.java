package com.hust.baseweb.applications.education.classmanagement.controller;

import com.hust.baseweb.applications.education.classmanagement.service.AssignmentService;
import com.hust.baseweb.applications.education.classmanagement.service.storage.exception.StorageException;
import com.hust.baseweb.applications.education.exception.ResponseFirstType;
import com.hust.baseweb.applications.education.exception.SimpleResponse;
import com.hust.baseweb.applications.education.model.CreateAssignmentIM;
import com.hust.baseweb.applications.education.model.DownloadSummissionsIM;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.websocket.server.PathParam;
import java.security.Principal;
import java.util.UUID;

@Log4j2
@Controller
@RequestMapping("/edu/assignment")
@AllArgsConstructor(onConstructor_ = @Autowired)
@CrossOrigin
public class AssignmentController {

    private final AssignmentService assignService;

    /**
     * Student submits assignment.
     *
     * @param name {@code id} of student (userId)
     * @param id   {@code id} of assignment
     * @param file
     * @return
     */
    @PostMapping("/{id}/submission")
    public ResponseEntity<?> submitAssignment(
        @CurrentSecurityContext(expression = "authentication.name") String name,
        @PathVariable UUID id,
        @RequestParam("file") MultipartFile file
    ) {
        SimpleResponse res;

        // FIXME: upgrade this method.
        try {
            res = assignService.saveSubmission(name, id, file);
        } catch (JpaSystemException e) {
            if ("fk_assignment_submission_assignment"
                .equals(e.getRootCause().getMessage().substring(94, 129))) {
                res = new SimpleResponse(
                    400,
                    "not exist",
                    "Bài tập không tồn tại");
            } else {
                res = new SimpleResponse(
                    500,
                    "unknown",
                    null);
            }
        }

        return ResponseEntity.status(res.getStatus()).body(res);
    }

    /*@GetMapping("/{id}/download-file/{filename:.+}")
    @ResponseBody
    public void downloadFile(@PathVariable UUID id, @PathVariable String filename, HttpServletResponse response) {
        response.setHeader("Content-Transfer-Encoding", "binary");
        response.setHeader(
            HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + filename + "\"");
        response.setContentType("application/octet-stream");

        try (InputStream is = storageService.loadFileAsResource(filename, id.toString())) {
            IOUtils.copyLarge(is, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    /**
     * Teacher download student's submissions.
     *
     * @param id    {@code id} of assignment
     * @param token X-Auth-Token
     * @param im
     */
    @PostMapping(value = "/{id}/submissions", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public ResponseEntity<StreamingResponseBody> downloadSubmissions(
        @PathVariable @NotBlank UUID id,
        @PathParam("token") @NotBlank String token,
        @Valid DownloadSummissionsIM im
    ) {
        String verifyResult = assignService.verifyDownloadPermission(id, token);

        if (null == verifyResult) {
            StreamingResponseBody stream = outputStream -> assignService.downloadSubmmissions(
                id.toString(),
                im.getStudentIds(),
                outputStream);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=result.zip");

            return ResponseEntity.ok().headers(headers).body(stream);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body(outputStream -> outputStream.write(verifyResult.getBytes()));
        }
    }

    @GetMapping("/{id}/student")
    public ResponseEntity<?> getAssignDetail(Principal principal, @PathVariable UUID id) {
        return ResponseEntity.ok().body(assignService.getAssignmentDetail(id, principal.getName()));
    }

    @GetMapping("/{id}/teacher")
    public ResponseEntity<?> getAssignDetail4Teacher(@PathVariable UUID id) {
        return ResponseEntity.ok().body(assignService.getAssignmentDetail4Teacher(id));
    }

    // CRUD.
    @Secured({"ROLE_EDUCATION_TEACHING_MANAGEMENT_TEACHER"})
    @PostMapping
    public ResponseEntity<?> createAssign(@RequestBody CreateAssignmentIM im) {
        ResponseFirstType res = assignService.createAssignment(im);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAssign(@PathVariable UUID id, @RequestBody CreateAssignmentIM im) {
        ResponseFirstType res = assignService.updateAssignment(id, im);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @Secured({"ROLE_EDUCATION_TEACHING_MANAGEMENT_TEACHER"})
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAssign(@PathVariable UUID id) {
        SimpleResponse res;

        /*try {
            res = assignService.deleteAssignment(id);
        } catch (DataIntegrityViolationException e) {
            res = new SimpleResponse(
                400,
                "not allowed",
                "Không thể xoá bài tập vì đã có sinh viên nộp bài");
        }*/

        res = assignService.deleteAssignment(id);

        return ResponseEntity.status(res.getStatus()).body(res);
    }

    // Handle exception.
    @ExceptionHandler(StorageException.class)
    public ResponseEntity<?> handleStorageException(StorageException e) {
        SimpleResponse res = new SimpleResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Failed to store file",
            e.getMessage());

        return ResponseEntity.status(res.getStatus()).body(res);
    }
}
