package vn.edu.hust.soict.judge0client.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import vn.edu.hust.soict.judge0client.config.Judge0Config;
import vn.edu.hust.soict.judge0client.entity.*;

import java.util.*;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(onConstructor_ = {@Autowired})
@EnableConfigurationProperties(Judge0Config.class)
public class Judge0ServiceImpl implements Judge0Service {

    RestTemplate restTemplate;

    Judge0Config judge0Config;

    /**
     * @param submission    If submission’s source_code, stdin or expected_output contains non-printable characters, or characters which cannot be sent with JSON, then set base64_encoded parameter to true and send these attributes Base64 encoded
     * @param base64Encoded
     * @param wait
     * @return
     * @throws HttpClientErrorException
     * @throws HttpServerErrorException
     */
    @Override
    public Judge0Submission createASubmission(Judge0Submission submission, Boolean base64Encoded, Boolean wait) throws HttpClientErrorException, HttpServerErrorException {
        if (submission == null) {
            return null;
        } else {
            String urlTemplate = judge0Config.getUri() + "/submissions";
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(urlTemplate);
            uriBuilder = addBase64Encoded(uriBuilder, base64Encoded);
            uriBuilder = addWait(uriBuilder, wait);

            String finalUrl = uriBuilder.build().toUriString();

            if (base64Encoded) {
                submission.encodeBase64();
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Judge0Submission> request = new HttpEntity<>(submission, headers);

            ResponseEntity<Judge0Submission> response = restTemplate.postForEntity(finalUrl, request, Judge0Submission.class);
            Judge0Submission responseBody = response.getBody();

            submission.setStdout(responseBody.getStdout());
            submission.setTime(responseBody.getTime());
            submission.setMemory(responseBody.getMemory());
            submission.setStderr(responseBody.getStderr());
            submission.setToken(responseBody.getToken());
            submission.setCompileOutput(responseBody.getCompileOutput());
            submission.setMessage(responseBody.getMessage());
            submission.setStatus(responseBody.getStatus());

//            if (base64Encoded) {
//                submission.decodeBase64();
//            }
            return submission;
        }
    }

//    private static void handleClientErrors(HttpClientErrorException ex) {
//        HttpStatus status = ex.getStatusCode();
//        String responseBody = ex.getResponseBodyAsString();
//
//        if (status == HttpStatus.UNPROCESSABLE_ENTITY) {
//            System.out.println("Validation Error: " + responseBody);
//            // Handle specific 422 cases by parsing the response body.
//            if (responseBody.contains("language_id")) {
//                if (responseBody.contains("can't be blank")) {
//                    System.out.println("Error: language_id can't be blank");
//                } else if (responseBody.contains("doesn't exist")) {
//                    System.out.println("Error: language_id does not exist");
//                }
//            } else if (responseBody.contains("wall_time_limit")) {
//                System.out.println("Error: wall_time_limit must be less than or equal to 150");
//            }
//        } else if (status == HttpStatus.BAD_REQUEST) {
//            System.out.println("Bad Request: wait not allowed");
//        } else if (status == HttpStatus.UNAUTHORIZED) {
//            System.out.println("Unauthorized access");
//        }
//    }
//
//    private static void handleServerErrors(HttpServerErrorException ex) {
//        HttpStatus status = ex.getStatusCode();
//        String responseBody = ex.getResponseBodyAsString();
//
//        if (status == HttpStatus.SERVICE_UNAVAILABLE) {
//            System.out.println("Service Unavailable: queue is full");
//        } else {
//            System.out.println("Server error: " + responseBody);
//        }
//    }


    @Override
    public Judge0Submission getASubmission(String token, Boolean base64Encoded, List<Judge0SubmissionFields> fields) {
        String urlTemplate = judge0Config.getUri() + "/submissions/{token}";
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(urlTemplate);
        uriBuilder = addBase64Encoded(uriBuilder, base64Encoded);
        uriBuilder = addFields(uriBuilder, fields);

        String finalUrl = uriBuilder.buildAndExpand(token).toUriString();

        Judge0Submission submission = restTemplate.getForObject(finalUrl, Judge0Submission.class);

        return submission;
    }

    /**
     * Not full check
     *
     * @param base64Encoded
     * @param page
     * @param perPage
     * @param fields
     * @return
     */
    @Override
    public Judge0SubmissionsPage getSubmissions(Boolean base64Encoded, Integer page, Integer perPage, List<Judge0SubmissionFields> fields) {
        String urlTemplate = judge0Config.getUri() + "/submissions/";
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(urlTemplate);
        uriBuilder = addBase64Encoded(uriBuilder, base64Encoded);
        uriBuilder = addPage(uriBuilder, page);
        uriBuilder = addPerPage(uriBuilder, perPage);
        uriBuilder = addFields(uriBuilder, fields);

        String finalUrl = uriBuilder.build().toUriString();

        ResponseEntity<Judge0SubmissionsPage> response = restTemplate.exchange(finalUrl, HttpMethod.GET, null, Judge0SubmissionsPage.class);
        return response.getBody();
    }

    /**
     * Temporary OK
     *
     * @param token
     * @param fields
     * @return
     */
    @Override
    public Judge0Submission deleteASubmission(String token, List<Judge0SubmissionFields> fields) {
        String urlTemplate = judge0Config.getUri() + "/submissions/{token}";
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(urlTemplate);
        uriBuilder = addFields(uriBuilder, fields);

        String finalUrl = uriBuilder.buildAndExpand(token).toUriString();

        ResponseEntity<Judge0Submission> response = restTemplate.exchange(finalUrl, HttpMethod.DELETE, null, Judge0Submission.class);
        return response.getBody();
    }

    @Override
    public List<Judge0Submission> createASubmissionBatch(Boolean base64Encoded, Judge0Submission... submissions) {
        if (submissions == null || submissions.length == 0) {
            return null;
        } else {
            String urlTemplate = judge0Config.getUri() + "/submissions/batch";
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(urlTemplate);
            uriBuilder = addBase64Encoded(uriBuilder, base64Encoded);

            String finalUrl = uriBuilder.build().toUriString();

            // Create the request body
            Judge0SubmissionBatch submissionRequest = new Judge0SubmissionBatch(submissions);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Judge0SubmissionBatch> requestEntity = new HttpEntity<>(submissionRequest, headers);

            Judge0Submission[] response = restTemplate.postForObject(finalUrl, requestEntity, Judge0Submission[].class);

            return response != null ? Arrays.stream(response)
//                    .peek(Judge0Submission::decodeBase64)
                    .collect(Collectors.toList())
                    : null;
        }
    }

    /**
     * @param tokens
     * @param base64Encoded
     * @param fields
     * @return
     */
    @Override
    public Judge0SubmissionBatch getASubmissionBatch(List<String> tokens, Boolean base64Encoded, List<Judge0SubmissionFields> fields) {
        UriComponentsBuilder urlTemplate = UriComponentsBuilder.fromHttpUrl(judge0Config.getUri() + "/submissions/batch");
        urlTemplate = addTokens(urlTemplate, tokens);
        urlTemplate = addBase64Encoded(urlTemplate, base64Encoded);
        urlTemplate = addFields(urlTemplate, fields);

        String finalUrl = urlTemplate.encode().toUriString();

        Judge0SubmissionBatch submissionBatch = restTemplate.getForObject(finalUrl, Judge0SubmissionBatch.class);
        return submissionBatch;
    }

    /**
     * @return
     */
    @Override
    public List<Judge0Language> getLanguages() {
        Judge0Language[] languages = restTemplate.getForObject(judge0Config.getUri() + "/languages", Judge0Language[].class);
        return Arrays.stream(languages).collect(Collectors.toList());
    }

    /**
     * @param id
     * @return
     */
    @Override
    public Judge0Language getALanguages(Integer id) {
        String urlTemplate = judge0Config.getUri() + "/languages/{id}";
        Map<String, Integer> uriVariables = new HashMap<>();
        uriVariables.put("id", id);

        return restTemplate.getForObject(urlTemplate, Judge0Language.class, uriVariables);
    }

    /**
     * @return
     */
    @Override
    public List<Judge0Language> getActiveAndArchivedLanguages() {
        Judge0Language[] languages = restTemplate.getForObject(judge0Config.getUri() + "/languages/all", Judge0Language[].class);
        return Arrays.stream(languages).collect(Collectors.toList());
    }

    /**
     * @return
     */
    @Override
    public List<Judge0Status> getStatuses() {
        Judge0Status[] statuses = restTemplate.getForObject(judge0Config.getUri() + "/statuses", Judge0Status[].class);
        return Arrays.stream(statuses).collect(Collectors.toList());
    }

    private UriComponentsBuilder addTokens(UriComponentsBuilder uriBuilder, List<String> tokens) {
        if (CollectionUtils.isEmpty(tokens)) {
            return uriBuilder.queryParam("tokens", "");
        } else {
            return uriBuilder.queryParam("tokens", tokens.stream().filter(Objects::nonNull).collect(Collectors.joining(",")));
        }
    }

    private UriComponentsBuilder addBase64Encoded(UriComponentsBuilder uriBuilder, Boolean base64Encoded) {
        return uriBuilder.queryParam("base64_encoded", String.valueOf(base64Encoded == null ? false : base64Encoded));
    }

    private UriComponentsBuilder addPage(UriComponentsBuilder uriBuilder, Integer page) {
        return uriBuilder.queryParam("page", String.valueOf(page == null || page < 1 ? 1 : page));
    }

    private UriComponentsBuilder addPerPage(UriComponentsBuilder uriBuilder, Integer perPage) {
        return uriBuilder.queryParam("per_page", String.valueOf(perPage == null || perPage < 1 ? 20 : perPage));
    }

    /**
     * wait = true => server chạy, wait = false => enqueue để worker chạy
     *
     * @param uriBuilder
     * @param wait
     * @return
     */
    private UriComponentsBuilder addWait(UriComponentsBuilder uriBuilder, Boolean wait) {
        return uriBuilder.queryParam("wait", String.valueOf(wait == null ? false : wait));
    }

    private UriComponentsBuilder addFields(UriComponentsBuilder builder, List<Judge0SubmissionFields> fields) {
        if (CollectionUtils.isEmpty(fields)) {
            return builder.queryParam("fields", "");
        } else {
            return builder.queryParam("fields", fields.stream()
                    .map(Judge0SubmissionFields::getField)
                    .collect(Collectors.joining(",")));
        }
    }
}
