package openerp.openerpresourceserver.recommend;

import openerp.openerpresourceserver.recommend.model.Course;

import java.util.List;

public interface RecommendationService {
    List<Course> getRecommendCourses(String studentId, String price, String rating, String duration);
}