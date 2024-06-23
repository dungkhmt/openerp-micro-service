package openerp.openerpresourceserver.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.entity.ContestSubmission;
import openerp.openerpresourceserver.entity.StudentSubmissionStatistics;
import openerp.openerpresourceserver.model.*;
import openerp.openerpresourceserver.repo.*;
import openerp.openerpresourceserver.service.StudentSubmissionStatisticsService;
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
public class StudentSubmissionStatisticsServiceImpl implements StudentSubmissionStatisticsService {
    private ContestSubmissionRepo contestSubmissionRepo;
    private SubmissionHourlySummaryRepo submissionHourlySummaryRepo;
    private ContestProblemRepo contestProblemRepo;
    private StudentSubmissionStatisticsRepo studentSubmissionStatisticsRepo;
    @Override
    public List<StudentSubmissionStatistics> getAllStatisticsDetailStudent() {
        return studentSubmissionStatisticsRepo.findAllByOrderByTotalSubmittedDesc();
    }
    @Override
    public StudentStatisticContest getStaticsContestStudentId(String studentId) {
        // Tính toán dữ liệu cần thiết từ cơ sở dữ liệu
        Set<String> uniqueContestIds = new HashSet<>();

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

            Collections.sort(studentSubmissionBySemsters, (submission1, submission2) -> {
                int month1 = submission1.getSubmissionMonth();
                int month2 = submission2.getSubmissionMonth();
                return Integer.compare(month1, month2);
            });

        }

        double totalSubmissionByMonthBySemster = 0;

        for (StudentSubmissionBySemester studentSubmissionBySemster : studentSubmissionBySemsters) {
            totalSubmissionByMonthBySemster = totalSubmissionByMonthBySemster + studentSubmissionBySemster.getNumberOfSubmissions();
        }

        int totalProblemSubmitted = 0;
        int totalSubmitted = 0;
        int totalProblemSubmittedAccept = 0;
        int totalProblemSubmittedPartial = 0;
        int totalProblemSubmittedCompileError = 0;
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


            dailySubmissionCounts.put(submissionDate, dailySubmissionCounts.getOrDefault(submissionDate, 0) + 1);

            if (firstSubmissionDate == null || submissionDate.isBefore(firstSubmissionDate)) {
                firstSubmissionDate = submissionDate;
            }
            if (lastSubmissionDate == null || submissionDate.isAfter(lastSubmissionDate)) {
                lastSubmissionDate = submissionDate;
            }
        }


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
        StudentStatisticContest studentStatisticContest = new StudentStatisticContest();
        studentStatisticContest.setStudentId(studentId);

        studentStatisticContest.setTotalSubmitted(totalSubmitted);
        studentStatisticContest.setTotalProblemSubmitted(totalProblemSubmittedMap.size());
        studentStatisticContest.setTotalProblemSubmittedAccept(totalProblemSubmittedAccept);
        studentStatisticContest.setTotalContestSubmitted(uniqueContestIds.size());
        studentStatisticContest.setTotalProblemSubmittedPartial(totalProblemSubmittedPartial);
        studentStatisticContest.setTotalProblemSubmittedCompileError(totalProblemSubmittedCompileError);
        studentStatisticContest.setContestDetails(contestDetails);
        studentStatisticContest.setAverageSubmissionPerDay(averageSubmissionPerDay);
        studentStatisticContest.setAverageMinimumSubmissionToAccept(averageMinimumSubmissionToAccept);
        studentStatisticContest.setDailySubmissionCounts(dailySubmissionCounts);
        studentStatisticContest.setTotalProblemSubmittedAccept(totalProblemSubmittedAcceptMap.size());
        studentStatisticContest.setFirstTimeAcceptList(totalProblemSubmittedAcceptMap);
        studentStatisticContest.setNumberProgramLanguage(contestSubmissionRepo.findNumberCountLanguagesDetailByUserId(studentId).length);
        studentStatisticContest.setProgrammingLanguageSubmitCounts(contestSubmissionRepo.findNumberCountLanguagesDetailByUserId(studentId));
        studentStatisticContest.setStudentSubmissionBySemsters(studentSubmissionBySemsters);
        studentStatisticContest.setSubmissionHourlySummary(submissionHourlySummaryRepo.submissionHourlySummariesByHourByUserID(studentId));
        return studentStatisticContest;
    }
}
