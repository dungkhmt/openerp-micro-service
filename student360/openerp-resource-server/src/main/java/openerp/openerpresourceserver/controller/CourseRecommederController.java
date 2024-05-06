package openerp.openerpresourceserver.controller;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.recommend.RecommendationService;
import openerp.openerpresourceserver.recommend.model.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/course-recommender")
public class CourseRecommederController {

    private RecommendationService RecommendationService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getStatisticsDetailStudentId(@PathVariable String id, @Param("price") String price) {
        if (price == null) {
            price = "";
        }
        List<Course> courses =  RecommendationService.getRecommendCourses(id, price);
        return ResponseEntity.ok().body(courses);
    }

}
