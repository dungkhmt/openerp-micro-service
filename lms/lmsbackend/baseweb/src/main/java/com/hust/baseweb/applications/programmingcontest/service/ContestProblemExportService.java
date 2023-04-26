package com.hust.baseweb.applications.programmingcontest.service;

import com.hust.baseweb.applications.contentmanager.repo.MongoContentService;
import com.hust.baseweb.applications.programmingcontest.constants.Constants;
import com.hust.baseweb.applications.programmingcontest.entity.ProblemEntity;
import com.hust.baseweb.applications.programmingcontest.entity.TagEntity;
import com.hust.baseweb.applications.programmingcontest.entity.TestCaseEntity;
import com.hust.baseweb.applications.programmingcontest.model.ModelCreateContestProblemResponse;
import com.hust.baseweb.applications.programmingcontest.repo.ProblemRepo;
import com.hust.baseweb.applications.programmingcontest.repo.TestCaseRepo;
import lombok.AllArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ContestProblemExportService {

    private final ProblemRepo problemRepo;
    private final TestCaseRepo testCaseRepo;
    private MongoContentService mongoContentService;

    public File exportProblemDescriptionToFile(ModelCreateContestProblemResponse problem) throws IOException {
        File file = new File("ProblemDescription.html");

        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        bufferedWriter.write(problem.getProblemDescription());

        bufferedWriter.close();
        fileWriter.close();

        return file;
    }

    public File exportProblemInfoToFile(ModelCreateContestProblemResponse problem) throws IOException {
        File file = new File("ProblemGeneralInformation.html");

        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        String s = "<p>Id: <em>" +
                   problem.getProblemId() +
                   "</em></p>" +
                   "<p><strong>Problem: " +
                   problem.getProblemName() +
                   "</strong></p>" +
                   "<p>Created at: <em>" +
                   problem.getCreatedAt() +
                   "</em> by <em>" +
                   problem.getUserId() +
                   "</em></p>" +
                   "<p>Public: <em>" +
                   problem.isPublicProblem() +
                   "</em></p>" +
                   "<p>Time limit: <em>" +
                   problem.getTimeLimit() +
                   "</em> s</p>" +
                   "<p>Memory limit: <em>" +
                   problem.getMemoryLimit() +
                   "</em> MB</p>" +
                   "<p>Level: <em>" +
                   problem.getLevelId() +
                   "</em></p>" +
                   "<p>Tags: <em>" +
                   problem.getTags().stream().map(TagEntity::getName).collect(Collectors.toList()) +
                   "</em></p>" +
                   "<p>Score evaluation type: <em>" +
                   problem.getScoreEvaluationType() +
                   "</em></p>" +
                   "<br/>" +
                   "<div style = \"padding: 12px; border: 2px gray solid\">" +
                   "<p><strong><em>Problem Description</em></strong></p>" +
                   problem.getProblemDescription() +
                   "</div>";

        bufferedWriter.write(s);

        bufferedWriter.close();
        fileWriter.close();

        return file;
    }

    public File exportProblemCorrectSolutionToFile(ModelCreateContestProblemResponse problem) throws IOException {
        String ext = Constants.Languages.mapLanguageToExtension(problem.getCorrectSolutionLanguage());
        File file = new File("Solution" + ext);

        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        bufferedWriter.write(problem.getCorrectSolutionSourceCode());

        bufferedWriter.close();
        fileWriter.close();

        return file;
    }

    public File exportProblemCustomCheckerToFile(ModelCreateContestProblemResponse problem) throws IOException {
        String ext = Constants.Languages.mapLanguageToExtension(problem.getSolutionCheckerSourceLanguage());
        File file = new File("CustomSolutionChecker" + ext);

        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        bufferedWriter.write(problem.getSolutionCheckerSourceCode());

        bufferedWriter.close();
        fileWriter.close();

        return file;
    }

    public List<File> exportProblemAttachmentToFile(ModelCreateContestProblemResponse problem) throws IOException {
        ProblemEntity problemEntity = problemRepo.findByProblemId(problem.getProblemId());
        List<File> attachments = new ArrayList<>();

        if (!problemEntity.getAttachment().isEmpty()) {
            String[] fileIds = problemEntity.getAttachment().split(";", -1);
            if (fileIds.length != 0) {
                for (String fileId : fileIds) {
                    GridFsResource content = mongoContentService.getById(fileId);
                    if (content != null) {
                        InputStream inputStream = content.getInputStream();

                        File file = new File(content.getFilename());
                        OutputStream outputFileStream = new FileOutputStream(file);
                        IOUtils.copy(inputStream, outputFileStream);

                        attachments.add(file);
                    }
                }
            }
        }

        return attachments;
    }

    public List<File> exportProblemTestCasesToFile(ModelCreateContestProblemResponse problem) throws IOException {
        String problemId = problem.getProblemId();

        List<TestCaseEntity> listTestCase = testCaseRepo.findAllByProblemId(problemId);
        List<File> listTestCaseFile = new ArrayList<>();

        for (int i = 0; i < listTestCase.size(); i++) {
            TestCaseEntity testCase = listTestCase.get(i);
            File file = new File(problemId + "_testcase_" + (i + 1) + ".txt");

            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            String s = "- Problem: " + problem.getProblemName() + "\n" +
                       "- Testcase point: " + testCase.getTestCasePoint() + "\n" +
                       "- Public: " + testCase.getIsPublic() + "\n" +
                       "- Status: " + testCase.getStatusId() + "\n" +
                       "- Description: " + testCase.getDescription() + "\n\n" +
                       "- Input: \n" + testCase.getTestCase() + "\n\n" +
                       "- Output: \n" + testCase.getCorrectAnswer() + "\n";

            bufferedWriter.write(s);

            bufferedWriter.close();
            fileWriter.close();

            listTestCaseFile.add(file);
        }

        return listTestCaseFile;
    }

}
