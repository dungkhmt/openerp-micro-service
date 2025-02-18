package vn.edu.hust.soict.judge0client.service;


import vn.edu.hust.soict.judge0client.entity.*;

import java.util.List;

public interface Judge0Service {

    Judge0Submission createASubmission(Judge0Submission submission, Boolean base64Encoded, Boolean wait);

    Judge0Submission getASubmission(String token, Boolean base64Encoded, List<Judge0SubmissionFields> fields);

    Judge0SubmissionsPage getSubmissions(Boolean base64Encoded, Integer page, Integer perPage, List<Judge0SubmissionFields> fields);

    Judge0Submission deleteASubmission(String token, List<Judge0SubmissionFields> fields);

    List<Judge0Submission> createASubmissionBatch(Boolean base64Encoded, Judge0Submission... submissions);

    Judge0SubmissionBatch getASubmissionBatch(List<String> tokens, Boolean base64Encoded, List<Judge0SubmissionFields> fields);

    List<Judge0Language> getLanguages();

    Judge0Language getALanguages(Integer id);

    List<Judge0Language> getActiveAndArchivedLanguages();

    List<Judge0Status> getStatuses();
}
