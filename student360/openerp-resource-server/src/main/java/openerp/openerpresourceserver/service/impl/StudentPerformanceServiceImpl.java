package openerp.openerpresourceserver.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.entity.ContestSubmission;
import openerp.openerpresourceserver.model.ContestSubmissionByStudent;
import openerp.openerpresourceserver.model.StudentPerformance;
import openerp.openerpresourceserver.model.StudentSemesterResult;
import openerp.openerpresourceserver.model.StudentSubmissionBySemester;
import openerp.openerpresourceserver.repo.ContestProblemRepo;
import openerp.openerpresourceserver.repo.ContestSubmissionRepo;
import openerp.openerpresourceserver.repo.SubmissionHourlySummaryRepo;
import openerp.openerpresourceserver.service.StudentPerformanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Log4j2
@AllArgsConstructor(onConstructor_ = @Autowired)
@Service
public class StudentPerformanceServiceImpl implements StudentPerformanceService {

    private ContestSubmissionRepo contestSubmissionRepo;
    private SubmissionHourlySummaryRepo submissionHourlySummaryRepo;
    private ContestProblemRepo contestProblemRepo;
    @Override
    public StudentPerformance getPerformanceStudentId(String studentId) {
        Set<String> uniqueContestIds = new HashSet<>();
        Set<String> uniqueProblemKey = new HashSet<>();

        Object hourLike = submissionHourlySummaryRepo.findMaxTotalSubmissionsByUserId(studentId);

        Object objectTimeActive = submissionHourlySummaryRepo.findStartEndTimeSubmittedByUserId(studentId);

        Object[] objArray = (Object[]) hourLike;

        String hourRange = (String) objArray[3];

        String hourRangePass = (String) objArray[4];

        Object[] timeActive = (Object[]) objectTimeActive;

        List<ContestSubmission> contestSubmissions = contestSubmissionRepo.findAllByUserSubmissionIdOrderByCreatedDateAsc(studentId);

        // Lấy dữ liệu mỗi ngày sinh viên submitted bao nhiêu lần
        Map<LocalDate, Integer> dailySubmissionCounts = new HashMap<>();
        Map<String, Integer> totalProblemSubmittedMap = new HashMap<>();
        Map<String, Integer> totalProblemSubmittedAcceptMap = new HashMap<>();
        Map<LocalTime, Integer> hourlySubmissionCounts = new HashMap<>();

        LocalDate firstSubmissionDate = null;
        LocalDate lastSubmissionDate = null;

        List<StudentSubmissionBySemester> studentSubmissionBySemsters = contestSubmissionRepo.findSemesterSubmissionHaveMaxSubmission(studentId, contestSubmissionRepo.findSemester(studentId));

        List<Integer> monthsSemsterOneInRange = new ArrayList<>(Arrays.asList(10, 11, 12, 1, 2, 3));
        List<Integer> monthsSemsterTwoInRange = new ArrayList<>(Arrays.asList(4, 5, 6, 7, 8));


        if (!studentSubmissionBySemsters.isEmpty()) {
            StudentSubmissionBySemester firstSubmission = studentSubmissionBySemsters.get(0);
            List<Integer> monthsInRange = firstSubmission.getSemester().endsWith("1") ? monthsSemsterOneInRange : monthsSemsterTwoInRange;

            List<Integer> existingMonths = new ArrayList<>();
            for (StudentSubmissionBySemester submission : studentSubmissionBySemsters) {
                existingMonths.add(submission.getSubmissionMonth());
            }

            monthsInRange.removeAll(existingMonths);
            for (int missingMonth : monthsInRange) {
                studentSubmissionBySemsters.add(new StudentSubmissionBySemester(
                        firstSubmission.getUserSubmissionId(),
                        firstSubmission.getSemester(),
                        missingMonth,
                        0L,
                        0
                ));
            }

            studentSubmissionBySemsters.sort((submission1, submission2) -> {
                int month1 = submission1.getSubmissionMonth();
                int month2 = submission2.getSubmissionMonth();
                return Integer.compare(month1, month2);
            });

        }


        Object[] submissionScoreHistory = contestSubmissionRepo.findScoreChangesInSubmissionByUserId(studentId);
        double averageSubmissionByMonthBySemster = 0;
        double totalSubmissionByMonthBySemster = 0;

        for (StudentSubmissionBySemester studentSubmissionBySemster : studentSubmissionBySemsters) {
            totalSubmissionByMonthBySemster = totalSubmissionByMonthBySemster + studentSubmissionBySemster.getNumberOfSubmissions();
        }

        averageSubmissionByMonthBySemster = totalSubmissionByMonthBySemster / studentSubmissionBySemsters.size();

        int n = 0;
        double semsterOneIn = 0;
        double semsterTwoIn = 0;


        for (StudentSubmissionBySemester studentSubmissionBySemster : studentSubmissionBySemsters) {
            double submissionDeviationByMonth = studentSubmissionBySemster.getNumberOfSubmissions() - averageSubmissionByMonthBySemster;
            studentSubmissionBySemster.setSubmissionDeviationByMonth(submissionDeviationByMonth);
            if ((studentSubmissionBySemster.getNumberOfSubmissions() > 0.5 * averageSubmissionByMonthBySemster) && (studentSubmissionBySemster.getNumberOfSubmissions() < 1.5*averageSubmissionByMonthBySemster)) {
                n++;
            }
            if (studentSubmissionBySemsters.get(1).getSemester().endsWith("1")) {
                if (studentSubmissionBySemster.getSubmissionMonth() == 2 || studentSubmissionBySemster.getSubmissionMonth() == 3) {
                    semsterOneIn += studentSubmissionBySemster.getNumberOfSubmissions();
                }
            }
            if (studentSubmissionBySemsters.get(1).getSemester().endsWith("2")) {
                if (studentSubmissionBySemster.getSubmissionMonth() == 7 || studentSubmissionBySemster.getSubmissionMonth() == 8) {
                    semsterTwoIn += studentSubmissionBySemster.getNumberOfSubmissions();
                }
            }
        }

        int totalProblemSubmitted = 0;
        int totalSubmitted = 0;
        int firstSubmissionProblemsWithScores = 0;
        double totalSubmisstionToAccept = 0;

        for (ContestSubmission contestSubmission : contestSubmissions) {
            LocalDate submissionDate = contestSubmission.getCreatedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalTime submissionTime = contestSubmission.getCreatedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalTime().truncatedTo(ChronoUnit.HOURS).plusHours(1);
            hourlySubmissionCounts.put(submissionTime, hourlySubmissionCounts.getOrDefault(submissionTime, 0) + 1);
            String problemId = contestSubmission.getProblemId();
            totalSubmitted++;
            uniqueContestIds.add(contestSubmission.getContestId());

            totalProblemSubmittedMap.put(problemId, totalProblemSubmittedMap.getOrDefault(problemId, 0) + 1);

            if ("Accept".equals(contestSubmission.getStatus()) && !totalProblemSubmittedAcceptMap.containsKey(problemId)) {
                totalProblemSubmittedAcceptMap.put(problemId, totalProblemSubmittedMap.get(problemId));
                totalSubmisstionToAccept += totalProblemSubmittedAcceptMap.get(problemId);
            }

            if (!uniqueProblemKey.contains(problemId)) {
                totalProblemSubmitted++;
                if (contestSubmission.getPoint() > 0) firstSubmissionProblemsWithScores++;
                uniqueProblemKey.add(problemId);
            }

            dailySubmissionCounts.put(submissionDate, dailySubmissionCounts.getOrDefault(submissionDate, 0) + 1);

            if (firstSubmissionDate == null || submissionDate.isBefore(firstSubmissionDate)) {
                firstSubmissionDate = submissionDate;
            }
            if (lastSubmissionDate == null || submissionDate.isAfter(lastSubmissionDate)) {
                lastSubmissionDate = submissionDate;
            }
        }

        int problemUpdateCount = 0;
        int problemEqualErrorScoreCount = 0;
        int totalsubmittedMultipleTimes = 0;

        for (Object submissionScore : submissionScoreHistory) {
            // Extract scores from submission data
            Object[] submissionArray = (Object[]) submissionScore;

            // Khai báo và khởi tạo giá trị mặc định cho initialScore và highestScore
            int initialScore = 0;
            int highestScore = 0;

            if ((long) submissionArray[1] > 2) totalsubmittedMultipleTimes++;

            String initialScoreString = (String) submissionArray[3];

            if (initialScoreString != null) {
                // Split và chuyển đổi điểm số ban đầu
                String[] initialScoreParts = initialScoreString.split("/");
                if (initialScoreParts.length > 0) {
                    try {
                        initialScore = Integer.parseInt(initialScoreParts[0].trim());
                    } catch (NumberFormatException ignored) {
                    }
                }
            }

            String highestScoreString = (String) submissionArray[2];

            if (highestScoreString != null) {
                String[] highestScoreParts = highestScoreString.split("/");
                if (highestScoreParts.length > 0) {
                    try {
                        highestScore = Integer.parseInt(highestScoreParts[0].trim());
                    } catch (NumberFormatException ignored) {
                    }
                }
            }

            if (initialScore < highestScore || initialScore > 0) {
                problemUpdateCount++;
            } else if (highestScore == 0) {
                problemEqualErrorScoreCount++;
            }
        }

        boolean hasProgress = (double) problemUpdateCount / totalProblemSubmitted > 0.5;
        boolean hasHighScore = (double) problemEqualErrorScoreCount / totalProblemSubmitted < 0.4;

        boolean submittedMultipleTimes;
        submittedMultipleTimes = (double) totalsubmittedMultipleTimes / totalProblemSubmitted > 0.5;

        double averageMinimumSubmissionToAccept = 0;
        if (totalProblemSubmittedAcceptMap.isEmpty()) {
            averageMinimumSubmissionToAccept = 0.0;
        } else {
            averageMinimumSubmissionToAccept = totalSubmisstionToAccept / totalProblemSubmittedAcceptMap.size();
        }


        if (firstSubmissionDate != null && lastSubmissionDate != null) {
            LocalDate currentDate = firstSubmissionDate;
            while (!currentDate.isAfter(lastSubmissionDate)) {
                dailySubmissionCounts.putIfAbsent(currentDate, 0);
                currentDate = currentDate.plusDays(1);
            }
        }

        assert firstSubmissionDate != null;
        long totalDays = ChronoUnit.DAYS.between(firstSubmissionDate, lastSubmissionDate) + 1;

        double averageSubmissionPerDay = (double) totalSubmitted / totalDays;

        int passState = -1;

        Object[] studentSemesterResultObject = contestSubmissionRepo.findStudentSemesterResult(studentId);
        List<StudentSemesterResult> studentSemesterResultList = new ArrayList<>();
        for (Object obj : studentSemesterResultObject) {

            Object[] data = (Object[]) obj;
            String userSubmissionId = (String) data[0];
            String semester = (String) data[1];
            double midtermPoint = ((BigDecimal) data[2]).doubleValue();
            double finalPoint = ((BigDecimal) data[3]).doubleValue();
            int appearedInPlagiarism = (int) data[4];
            int passingState = (int) data[5];
            if (passingState == 1)
                passState = 1;

            // Create a new StudentSemesterResult object
            StudentSemesterResult result = new StudentSemesterResult();
            result.setUserSubmissionId(userSubmissionId);
            result.setSemester(semester);
            result.setMidtermPoint(midtermPoint);
            result.setFinalPoint(finalPoint);
            result.setAppearedInPlagiarism(appearedInPlagiarism);
            result.setPassingState(passingState);

            // Add the object to the list
            studentSemesterResultList.add(result);
        }


        List<ContestSubmissionByStudent> contestDetails = new ArrayList<>();

        for (String contestId : uniqueContestIds) {
            ContestSubmissionByStudent modelContestSubmission = new ContestSubmissionByStudent();
            modelContestSubmission.setContestId(contestId);
            modelContestSubmission.setTotalProblems(contestProblemRepo.countByContestId(contestId));
            List<ContestSubmission> studentContestSubmissions = contestSubmissionRepo.findByContestIdAndUserSubmissionId(contestId, studentId);
            Set<String> uniqueProblemIds = new HashSet<>();
            for (ContestSubmission submission : studentContestSubmissions) {
                uniqueProblemIds.add(submission.getProblemId());
            }
            modelContestSubmission.setTotalProblemsSubmitted(uniqueProblemIds.size());
            contestDetails.add(modelContestSubmission);
        }
        // Tạo đối tượng StudentSubmissionDetail và ánh xạ dữ liệu
        StudentPerformance studentPerformance = new StudentPerformance();
        studentPerformance.setStudentId(studentId);

        studentPerformance.setPassState(passState);
        studentPerformance.setTotalSubmitted(totalSubmitted);
        studentPerformance.setTotalProblemSubmitted(totalProblemSubmitted);

        if (n >=5) {
            studentPerformance.setLearningBehavior("Học đều xuyên suốt kỳ");
        }
        else if ((semsterOneIn > 3*(averageSubmissionByMonthBySemster)) || (semsterTwoIn > 3*(averageSubmissionByMonthBySemster)))  {
            studentPerformance.setLearningBehavior("Xu hướng học dồn vào cuối kỳ");
        }
        else studentPerformance.setLearningBehavior("Học không đều");

        studentPerformance.setFirstSubmissionAccuracyRate((double) 100 * firstSubmissionProblemsWithScores / totalProblemSubmitted);
        studentPerformance.setAverageSubmissionPerDay(averageSubmissionPerDay);
        studentPerformance.setAverageMinimumSubmissionToAccept(averageMinimumSubmissionToAccept);
        studentPerformance.setNumberProgramLanguage(contestSubmissionRepo.findNumberCountLanguagesDetailByUserId(studentId).length);
        studentPerformance.setProgrammingLanguageSubmitCounts(contestSubmissionRepo.findNumberCountLanguagesDetailByUserId(studentId) != null ? contestSubmissionRepo.findNumberCountLanguagesDetailByUserId(studentId) : new List[]{Collections.emptyList()});
        studentPerformance.setMostSubmittedTime(hourRange);
        studentPerformance.setMostEffectiveSubmittedTime(hourRangePass);
        studentPerformance.setStartTimeActive((String) timeActive[1]);
        studentPerformance.setEndTimeActive((String) timeActive[2]);
        studentPerformance.setSubmittedMultipleTimes(submittedMultipleTimes);
        studentPerformance.setHasHighScore(hasHighScore);
        studentPerformance.setHasProgress(hasProgress);
        studentPerformance.setStudentSemesterResult(studentSemesterResultList);
        return studentPerformance;
    }


}
