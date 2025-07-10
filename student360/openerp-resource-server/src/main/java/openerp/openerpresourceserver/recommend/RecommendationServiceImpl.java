package openerp.openerpresourceserver.recommend;

import openerp.openerpresourceserver.model.StudentPerformance;
import openerp.openerpresourceserver.recommend.util.CoursesLoader;
import openerp.openerpresourceserver.service.StudentPerformanceService;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.recommend.model.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@AllArgsConstructor(onConstructor_ = @Autowired)
@Service
public class RecommendationServiceImpl implements RecommendationService {

    @Autowired
    private StudentPerformanceService studentPerformanceService;
    @Override
    public List<Course> getRecommendCourses(String studentId, String price, String rating, String duration) {
        List<Course> courses = CoursesLoader.loadCoursesFromCSV("course.csv");
        StudentPerformance student = studentPerformanceService.getPerformanceStudentId(studentId);
        List<Course> recommendedCourses;
        double defaultRating = (rating != null) ? Double.parseDouble(rating) : 4.0;
        double defaultHour = (duration != null) ? Double.parseDouble(duration) : 3.0;

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
            recommendedCourses = courses.stream()
                    .filter(course -> course.getRating() >= defaultRating)
                    .filter(course -> {
                        if (defaultHour == 0)
                            return course.getHours() < 1 && course.getHours() >= 0;
                        else if (defaultHour == 1)
                            return course.getHours() > 1 && course.getHours() <= 3;
                        if (defaultHour == 3)
                            return course.getHours() > 3 && course.getHours() <= 6;
                        else if (defaultHour == 6)
                            return course.getHours() > 6 && course.getHours() <= 17;
                        return course.getHours() > 17;
                    })
                    .sorted(Comparator.comparingDouble(Course::getBasicTfIdfScore).reversed())
                    .limit(10)
                    .collect(Collectors.toList());
        }
        else {
            recommendedCourses = courses.stream()
                    .filter(course -> course.getRating() >= defaultRating)
                    .filter(course -> {
                        if (defaultHour == 0)
                            return course.getHours() < 1 && course.getHours() >= 0;
                        else if (defaultHour == 1)
                            return course.getHours() > 1 && course.getHours() <= 3;
                        if (defaultHour == 3)
                            return course.getHours() > 3 && course.getHours() <= 6;
                        else if (defaultHour == 6)
                            return course.getHours() > 6 && course.getHours() <= 17;
                        return course.getHours() > 17;
                    })
                    .sorted(Comparator.comparingDouble(Course::getAdvanceTfIdfScore).reversed())
                    .limit(10)
                    .collect(Collectors.toList());

        }
        return recommendedCourses;
    }
}
