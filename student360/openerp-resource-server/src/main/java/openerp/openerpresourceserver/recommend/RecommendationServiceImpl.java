package openerp.openerpresourceserver.recommend;

import openerp.openerpresourceserver.model.StudentPerformance;
import openerp.openerpresourceserver.service.StudentSubmissionStatisticsService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.recommend.model.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@AllArgsConstructor(onConstructor_ = @Autowired)
@Service
public class RecommendationServiceImpl implements RecommendationService {

    @Autowired
    private StudentSubmissionStatisticsService studentSubmissionStatisticsService;
    @Override
    public List<Course> getRecommendCourses(String studentId, String price) {
        List<Course> courses = loadCoursesFromCSV("course.csv");
        StudentPerformance student = studentSubmissionStatisticsService.getPerformanceStudentId(studentId);
        List<Course> recommendedCourses = new ArrayList<>();

        if (price.equalsIgnoreCase("free")) {
            courses = courses.stream()
                    .filter(course -> "0".equals(course.getCurrentPrice()))
                    .toList();
        }
        else {
            courses = courses.stream()
                    .filter(course -> ! "0".equals(course.getCurrentPrice()))
                    .toList();
        }

        if (student.getPassState() != 1) {
            List<Course> beginnerCourses = courses.stream()
                    .filter(course -> course.getLevel().equalsIgnoreCase("beginner"))
                    .toList();

            // Lọc các khóa học có title chứa các từ khóa quan trọng và không nằm trong beginnerCourses
            List<String> allDescriptions = courses.stream()
                    .map(Course::getSubtitle)
                    .toList();

            List<String> allKeywords = new ArrayList<>();

            for (String description : allDescriptions) {
                String[] words = description.toLowerCase().split("\\s+");
                for (String word : words) {
                    // You can add more conditions to filter out irrelevant words
                    if (word.equals("simple") || word.equals("basic") || word.equals("begin") || word.equals("zero") || word.equals("part 1") || word.equals("essentials")) {
                        allKeywords.add(word);
                    }
                }
            }
            Map<String, Long> keywordCount = allKeywords.stream()
                    .collect(Collectors.groupingBy(w -> w, Collectors.counting()));
            List<String> topKeywords = keywordCount.entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .map(Map.Entry::getKey)
                    .toList();

            List<Course> filteredCoursesByTitle = courses.stream()
                    .filter(course -> topKeywords.stream().anyMatch(course.getTitle().toLowerCase()::contains))
                    .filter(course -> !beginnerCourses.contains(course))
                    .toList();

            List<Course> filteredCoursesBySubtitle = courses.stream()
                    .filter(course -> topKeywords.stream().anyMatch(course.getSubtitle().toLowerCase()::contains))
                    .filter(course -> !beginnerCourses.contains(course))
                    .toList();

            // Kết hợp beginnerCourses và filteredCourses
            recommendedCourses.addAll(beginnerCourses);
            recommendedCourses.addAll(filteredCoursesByTitle);
            recommendedCourses.addAll(filteredCoursesBySubtitle);

            // Giới hạn số lượng khóa học trả về là 20
            recommendedCourses = recommendedCourses.stream()
                    .limit(20)
                    .collect(Collectors.toList());
        }
        else {
            List<Course> intermediateCourses = courses.stream()
                    .filter(course -> course.getLevel().equalsIgnoreCase("intermediate"))
                    .toList();

            // Lọc các khóa học có title chứa các từ khóa quan trọng và không nằm trong beginnerCourses
            List<String> allDescriptions = courses.stream()
                    .map(Course::getSubtitle)
                    .toList();

            List<String> allKeywords = new ArrayList<>();

            for (String description : allDescriptions) {
                String[] words = description.toLowerCase().split("\\s+");
                for (String word : words) {
                    // You can add more conditions to filter out irrelevant words
                    if (word.equals("advance") || word.equals("mastery")) {
                        allKeywords.add(word);
                    }
                }
            }
            Map<String, Long> keywordCount = allKeywords.stream()
                    .collect(Collectors.groupingBy(w -> w, Collectors.counting()));
            List<String> topKeywords = keywordCount.entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .map(Map.Entry::getKey)
                    .toList();

            List<Course> filteredCoursesByTitle = courses.stream()
                    .filter(course -> topKeywords.stream().anyMatch(course.getTitle().toLowerCase()::contains))
                    .filter(course -> !intermediateCourses.contains(course))
                    .toList();

            List<Course> filteredCoursesBySubtitle = courses.stream()
                    .filter(course -> topKeywords.stream().anyMatch(course.getSubtitle().toLowerCase()::contains))
                    .filter(course -> !intermediateCourses.contains(course))
                    .toList();

            // Kết hợp beginnerCourses và filteredCourses
            recommendedCourses.addAll(intermediateCourses);
            recommendedCourses.addAll(filteredCoursesByTitle);
            recommendedCourses.addAll(filteredCoursesBySubtitle);

            // Giới hạn số lượng khóa học trả về là 20
            recommendedCourses = recommendedCourses.stream()
                    .limit(20)
                    .collect(Collectors.toList());

        }
        return recommendedCourses;
    }

    public List<Course> loadCoursesFromCSV(String csvFileName) {
        List<Course> courses = new ArrayList<>();
        BufferedReader br = null;

        try {
            // Load file từ thư mục resources
            Resource resource = new ClassPathResource(csvFileName);
            InputStream inputStream = resource.getInputStream();
            br = new BufferedReader(new InputStreamReader(inputStream));

            // Sử dụng Apache Commons CSV để parse CSV
            CSVParser csvParser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(br);

            for (CSVRecord csvRecord : csvParser) {
                Course course = new Course();
                course.setId(csvRecord.get("id"));
                course.setTitle(csvRecord.get("title"));
                course.setUrl(csvRecord.get("url"));
                course.setSubtitle(csvRecord.get("subtitle"));
                course.setRating(csvRecord.get("rating"));
                course.setReviews(csvRecord.get("reviews"));
                course.setHours(csvRecord.get("hours"));
                course.setLectures(csvRecord.get("lectures"));
                course.setLevel(csvRecord.get("level"));
                course.setCurrentPrice(csvRecord.get("currentPrice"));
                course.setOriginalPrice(csvRecord.get("originalPrice"));
                courses.add(course);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.err.println("Lỗi khi phân tích dữ liệu: " + e.getMessage());
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return courses;
    }
}
