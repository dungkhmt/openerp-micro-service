package openerp.openerpresourceserver.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.entity.ContestSubmission;
import openerp.openerpresourceserver.entity.User;
import openerp.openerpresourceserver.model.*;
import openerp.openerpresourceserver.repo.ContestProblemRepo;
import openerp.openerpresourceserver.repo.ContestSubmissionRepo;
import openerp.openerpresourceserver.repo.UserRepo;
import openerp.openerpresourceserver.repo.SubmissionHourlySummaryRepo;
import openerp.openerpresourceserver.service.StudentSubmissionStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@AllArgsConstructor(onConstructor_ = @Autowired)
@Service
public class StudentSubmissionStatisticsServiceImpl implements StudentSubmissionStatisticsService {
    private ContestSubmissionRepo contestSubmissionRepo;
    private SubmissionHourlySummaryRepo submissionHourlySummaryRepo;
    private ContestProblemRepo contestProblemRepo;
    private UserRepo userRepo;

    @Override
    public List<StudentSubmissionDetail> getAllStatisticsDetailStudent() {

        List<StudentSubmissionDetail> allStudentDetails = new ArrayList<>();

        submissionHourlySummaryRepo.deleteAllData();
        submissionHourlySummaryRepo.updateSummaryData();

        Set<String> uniqueStudentKey = new HashSet<>();

        List<ContestSubmission> contestSubmissionAll = contestSubmissionRepo.findAll();


        for (ContestSubmission contest : contestSubmissionAll) {
            uniqueStudentKey.add(contest.getUserSubmissionId());
        }

        for (String studentId : uniqueStudentKey) {

            Set<String> uniqueContestIds = new HashSet<>();
            Set<String> uniqueProblemKey = new HashSet<>();

            List<ContestSubmission> contestSubmissions = contestSubmissionRepo.findByUserSubmissionId(studentId);

            // Lấy dữ liệu mỗi ngày sinh viên submitted bao nhiêu lần
            Map<LocalDate, Integer> dailySubmissionCounts = new HashMap<>();
            Map<String, Integer> totalProblemSubmittedMap = new HashMap<>();
            Map<String, Integer> totalProblemSubmittedAcceptMap = new HashMap<>();

            LocalDate firstSubmissionDate = null;
            LocalDate lastSubmissionDate = null;

            int totalSubmitted = 0;
            int totalProblemSubmitted = 0;
            int totalProblemSubmittedPartial = 0;
            int totalProblemSubmittedCompileError = 0;
            int firstSubmissionProblemsWithScores = 0;
            double totalSubmisstionToAccept = 0;


            for (ContestSubmission contestSubmission : contestSubmissions) {
                LocalDate submissionDate = contestSubmission.getCreatedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                String problemId = contestSubmission.getProblemId();
                totalSubmitted++;
                uniqueContestIds.add(contestSubmission.getContestId());
                totalProblemSubmittedMap.put(problemId, totalProblemSubmittedMap.getOrDefault(problemId, 0) + 1);

//              Nếu Contest accept và chưa có trong dãy totalProblemSubmittedAcceptMap thì problemAccept
                if ("Accept".equals(contestSubmission.getStatus()) && !totalProblemSubmittedAcceptMap.containsKey(problemId)) {
                    totalProblemSubmittedAcceptMap.put(problemId, totalProblemSubmittedMap.get(problemId));
                    totalSubmisstionToAccept += totalProblemSubmittedAcceptMap.get(problemId);
                }
//
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


            double averageMinimumSubmissionToAccept = 0;

            if (totalProblemSubmittedAcceptMap.isEmpty()) {
                // Trường hợp map rỗng, không thể chia được
                averageMinimumSubmissionToAccept = 0.0; // hoặc bạn có thể xử lý theo logic của ứng dụng của mình
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

            long totalDays = ChronoUnit.DAYS.between(firstSubmissionDate, lastSubmissionDate) + 1;

            double averageSubmissionPerDay = (double) totalSubmitted / totalDays;

            contestSubmissions = contestSubmissionRepo.findByUserSubmissionId(studentId);

            for (ContestSubmission contestSubmission : contestSubmissions) {
                uniqueContestIds.add(contestSubmission.getContestId());
            }

            List<ModelContestSubmission> contestDetails = new ArrayList<>();
            for (String contestId : uniqueContestIds) {
                ModelContestSubmission modelContestSubmission = new ModelContestSubmission();
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

            StudentSubmissionDetail studentSubmissionDetail = new StudentSubmissionDetail();

            studentSubmissionDetail.setStudentId(studentId);

//            Tổng số submission
            studentSubmissionDetail.setTotalSubmitted(totalSubmitted);

//            Tổng số problem đã submission
            studentSubmissionDetail.setTotalProblemSubmitted(totalProblemSubmitted);

//            Tổng số contest đã submission
            studentSubmissionDetail.setTotalContestSubmitted(uniqueContestIds.size());

//            Ty le submission lan dau tien thanh cong
            studentSubmissionDetail.setFirstSubmissionAccuracyRate((double) 100 * firstSubmissionProblemsWithScores / totalProblemSubmitted);

//            Tổng số problem đã accept
            studentSubmissionDetail.setTotalProblemSubmittedAccept(totalProblemSubmittedAcceptMap.size());

//            so problem khong bi khong
            studentSubmissionDetail.setTotalProblemSubmittedPartial(totalProblemSubmittedPartial);

//            Trung bình cần ít nhất bao nhieu submission de ACCEPT
            studentSubmissionDetail.setAverageMinimumSubmissionToAccept(averageMinimumSubmissionToAccept);

//            số problem bị lỗi
            studentSubmissionDetail.setTotalProblemSubmittedCompileError(totalProblemSubmittedCompileError);

//            Ngay submission dau tien
            studentSubmissionDetail.setFirstSubmissionDate(firstSubmissionDate);

//            Ngay submission cuoi cung
            studentSubmissionDetail.setLastSubmissionDate(lastSubmissionDate);

//            Trung binh 1 ngay bao nhieu submission
            studentSubmissionDetail.setAverageSubmissionPerDay(averageSubmissionPerDay);

//            So ngon ngu lap trinh
            studentSubmissionDetail.setNumberProgramLanguage(contestSubmissionRepo.findNumberCountLanguagesDetailByUserId(studentId).length);

            allStudentDetails.add(studentSubmissionDetail);
        }

        return allStudentDetails;
    }

    @Override
    public StudentSubmissionDetail getStatisticsDetailStudentId(String studentId) {
        // Tính toán dữ liệu cần thiết từ cơ sở dữ liệu
        User user = userRepo.findAllById(studentId);
        Set<String> uniqueContestIds = new HashSet<>();
        Set<String> uniqueProblemKey = new HashSet<>();

        Object hourLike = submissionHourlySummaryRepo.findMaxTotalSubmissionsByUserId(studentId);

        Object objectTimeActive = submissionHourlySummaryRepo.findStartEndTimeSubmittedByUserId(studentId);

        Object[] objArray = (Object[]) hourLike;

        // Lấy giá trị từ chỉ mục thứ hai của mảng
        String hourRange = (String) objArray[3];

        String hourRangePass = (String) objArray[4];

        Object[] timeActive = (Object[]) objectTimeActive;

        List<ContestSubmission> contestSubmissions = contestSubmissionRepo.findByUserSubmissionId(studentId);

        // Lấy dữ liệu mỗi ngày sinh viên submitted bao nhiêu lần
        Map<LocalDate, Integer> dailySubmissionCounts = new HashMap<>();
        Map<String, Integer> totalProblemSubmittedMap = new HashMap<>();
        Map<String, Integer> totalProblemSubmittedAcceptMap = new HashMap<>();
        Map<String, Integer> programmingLanguageSubmitCountMap = new HashMap<>();
        Map<LocalTime, Integer> hourlySubmissionCounts = new HashMap<>();

        LocalDate firstSubmissionDate = null;
        LocalDate lastSubmissionDate = null;

        List<StudentSubmissionBySemster> studentSubmissionBySemsters = contestSubmissionRepo.findSemesterSubmissionHaveMaxSubmission(studentId, contestSubmissionRepo.findSemester(studentId));

        List<Integer> monthsSemsterOneInRange = new ArrayList<>(Arrays.asList(10, 11, 12, 1, 2, 3));
        List<Integer> monthsSemsterTwoInRange = new ArrayList<>(Arrays.asList(4, 5, 6, 7, 8));


        if (!studentSubmissionBySemsters.isEmpty()) {
            StudentSubmissionBySemster firstSubmission = studentSubmissionBySemsters.get(0);
            List<Integer> monthsInRange = firstSubmission.getSemester().endsWith("1") ? monthsSemsterOneInRange : monthsSemsterTwoInRange;

            List<Integer> existingMonths = new ArrayList<>();
            for (StudentSubmissionBySemster submission : studentSubmissionBySemsters) {
                existingMonths.add(submission.getSubmissionMonth());
            }

            monthsInRange.removeAll(existingMonths);
            for (int missingMonth : monthsInRange) {
                studentSubmissionBySemsters.add(new StudentSubmissionBySemster(
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


        Object[] submissionScoreHistory = contestSubmissionRepo.findScoreChangesInSubmissionByUserId(studentId);
        double averageSubmissionByMonthBySemster = 0;
        double totalSubmissionByMonthBySemster = 0;

        for (StudentSubmissionBySemster studentSubmissionBySemster : studentSubmissionBySemsters) {
            totalSubmissionByMonthBySemster = totalSubmissionByMonthBySemster + studentSubmissionBySemster.getNumberOfSubmissions();
        }

        averageSubmissionByMonthBySemster = totalSubmissionByMonthBySemster / studentSubmissionBySemsters.size();

        int n = 0;
        double semsterOneIn = 0;
        double semsterTwoIn = 0;


        for (StudentSubmissionBySemster studentSubmissionBySemster : studentSubmissionBySemsters) {
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
        int totalProblemSubmittedAccept = 0;
        int totalProblemSubmittedPartial = 0;
        int totalProblemSubmittedCompileError = 0;
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
                    } catch (NumberFormatException e) {
                    }
                }
            }

            String highestScoreString = (String) submissionArray[2];

            if (highestScoreString != null) {
                String[] highestScoreParts = highestScoreString.split("/");
                if (highestScoreParts.length > 0) {
                    try {
                        highestScore = Integer.parseInt(highestScoreParts[0].trim());
                    } catch (NumberFormatException e) {
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

        List<StudentSemesterResult> studentSemesterResult = contestSubmissionRepo.findStudentSemesterResult(studentId);
        List<Double> semesterMidtermPoint = contestSubmissionRepo.findMidtermPointByUserId(studentId);
        List<Double> semesterFinalPoint = contestSubmissionRepo.findFinalPointByUserId(studentId);
        for(int i = 0; i < studentSemesterResult.size(); i++) {
            StudentSemesterResult result = studentSemesterResult.get(i);
            double midtermPoint = !semesterMidtermPoint.isEmpty() && semesterMidtermPoint.get(i) != null ? semesterMidtermPoint.get(i) : 0.0;
            double finalPoint = !semesterFinalPoint.isEmpty() && semesterFinalPoint.get(i) != null ? semesterFinalPoint.get(i) : 0.0;
            result.setMidtermPoint(midtermPoint);
            result.setFinalPoint(finalPoint);
            if (result.getMidtermPoint() > 2.5 && result.getFinalPoint() > 2.5 && result.getMidtermPoint()*0.3 + result.getFinalPoint()*0.3 > 3.5) {
                result.setPassingState(1);
                passState = 1;
            }
            else {
                result.setPassingState(-1);
            }
        }

        // Tạo đối tượng StudentSubmissionDetail và ánh xạ dữ liệu
        StudentSubmissionDetail studentSubmissionDetail = new StudentSubmissionDetail();
        studentSubmissionDetail.setStudentId(studentId);

        studentSubmissionDetail.setPassState(passState);
        studentSubmissionDetail.setTotalSubmitted(totalSubmitted);
        studentSubmissionDetail.setTotalProblemSubmitted(totalProblemSubmitted);
        studentSubmissionDetail.setTotalProblemSubmittedAccept(totalProblemSubmittedAccept);
        studentSubmissionDetail.setTotalProblemSubmittedPartial(totalProblemSubmittedPartial);
        studentSubmissionDetail.setTotalProblemSubmittedCompileError(totalProblemSubmittedCompileError);

        contestSubmissions = contestSubmissionRepo.findByUserSubmissionId(studentId);

        for (ContestSubmission contestSubmission : contestSubmissions) {
            uniqueContestIds.add(contestSubmission.getContestId());
        }
        studentSubmissionDetail.setTotalContestSubmitted(uniqueContestIds.size());

        List<ModelContestSubmission> contestDetails = new ArrayList<>();

        for (String contestId : uniqueContestIds) {
            ModelContestSubmission modelContestSubmission = new ModelContestSubmission();
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

        if (n >=5) {
            studentSubmissionDetail.setLearningBehavior("Học đều xuyên suot ky");
        }
        else if ((semsterOneIn > 3*(averageSubmissionByMonthBySemster)) || (semsterTwoIn > 3*(averageSubmissionByMonthBySemster)))  {
            studentSubmissionDetail.setLearningBehavior("Xu hướng học dồn vào cuối kỳ");
        }
        else studentSubmissionDetail.setLearningBehavior("Hoc không đều");

        studentSubmissionDetail.setEmail(user != null ? user.getEmail() : null);
        studentSubmissionDetail.setFullname(user != null ? user.getFullName() : null);
        studentSubmissionDetail.setContestDetails(contestDetails);
        studentSubmissionDetail.setFirstSubmissionDate(firstSubmissionDate);
        studentSubmissionDetail.setLastSubmissionDate(lastSubmissionDate);
        studentSubmissionDetail.setFirstSubmissionAccuracyRate((double) 100 * firstSubmissionProblemsWithScores / totalProblemSubmitted);
        studentSubmissionDetail.setAverageSubmissionPerDay(averageSubmissionPerDay);
        studentSubmissionDetail.setAverageMinimumSubmissionToAccept(averageMinimumSubmissionToAccept);
        studentSubmissionDetail.setDailySubmissionCounts(dailySubmissionCounts);
        studentSubmissionDetail.setTotalProblemSubmittedAccept(totalProblemSubmittedAcceptMap.size());
        studentSubmissionDetail.setFirstTimeAcceptList(totalProblemSubmittedAcceptMap);
        studentSubmissionDetail.setNumberProgramLanguage(contestSubmissionRepo.findNumberCountLanguagesDetailByUserId(studentId).length);
        studentSubmissionDetail.setProgrammingLanguageSubmitCounts(contestSubmissionRepo.findNumberCountLanguagesDetailByUserId(studentId));
        studentSubmissionDetail.setHourlySubmissionCounts(hourlySubmissionCounts);
        studentSubmissionDetail.setMostSubmittedTime(hourRange);
        studentSubmissionDetail.setMostEffectiveSubmittedTime(hourRangePass);
        studentSubmissionDetail.setStartTimeActive((String) timeActive[1]);
        studentSubmissionDetail.setEndTimeActive((String) timeActive[2]);
        studentSubmissionDetail.setSubmissionScoreHistory(submissionScoreHistory);
        studentSubmissionDetail.setSubmittedMultipleTimes(submittedMultipleTimes);
        studentSubmissionDetail.setHasHighScore(hasHighScore);
        studentSubmissionDetail.setHasProgress(hasProgress);
        studentSubmissionDetail.setStudentSubmissionBySemsters(studentSubmissionBySemsters);
        studentSubmissionDetail.setSubmissionHourlySummary(submissionHourlySummaryRepo.submissionHourlySummariesByHourByUserID(studentId));
        studentSubmissionDetail.setStudentSemesterResult(studentSemesterResult);
        return studentSubmissionDetail;
    }
    public static double calculateStandardDeviationBySubmissions(List<StudentSubmissionBySemster> studentSubmissionBySemsters) {
        if (studentSubmissionBySemsters == null || studentSubmissionBySemsters.isEmpty()) {
            throw new IllegalArgumentException("List cannot be null or empty");
        }

        // Lấy danh sách số lần nộp bài theo tháng
        List<Long> numberOfSubmissionsPerMonth = studentSubmissionBySemsters.stream()
                .map(submission -> submission.getNumberOfSubmissions())
                .collect(Collectors.toList());

        // Tính toán trung bình số lần nộp bài theo tháng
        double averageSubmissionsPerMonth = numberOfSubmissionsPerMonth.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0); // Trả về 0.0 nếu danh sách rỗng

        // Tính toán bình phương độ lệch của từng tháng so với trung bình
        List<Double> squaredDeviations = studentSubmissionBySemsters.stream()
                .map(submission -> Math.pow(submission.getNumberOfSubmissions() - averageSubmissionsPerMonth, 2))
                .collect(Collectors.toList());

        // Tính tổng bình phương độ lệch
        double squaredDeviationsSum = squaredDeviations.stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        // Kiểm tra nếu chỉ có 1 phần tử (không thể tính độ lệch chuẩn)
        if (studentSubmissionBySemsters.size() == 1) {
            return 0.0;
        }

        // Tính độ lệch chuẩn (population standard deviation)
        double variance = squaredDeviationsSum / (studentSubmissionBySemsters.size() - 1);
        return Math.sqrt(variance);
    }
}
