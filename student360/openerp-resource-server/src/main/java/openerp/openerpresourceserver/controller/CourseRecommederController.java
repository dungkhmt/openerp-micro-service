package openerp.openerpresourceserver.controller;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.recommend.RecommendationService;
import openerp.openerpresourceserver.recommend.model.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/course-recommender")
public class CourseRecommederController {

    private RecommendationService RecommendationService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getStatisticsDetailStudentId(
            @PathVariable String id,
            @RequestParam(name = "price", required = false) String price,
            @RequestParam(name = "rating", required = false) String rating,
            @RequestParam(name = "duration", required = false) String duration)
    {
        if (price == null || price.equals("undefined")) {
            price = " ";
        }
        if (rating == null || rating.equals("undefined")) {
            rating = "4.0";
        }

        if (duration == null || duration.equals("undefined")) {
            duration = "3.0";
        }

        List<Course> courses =  RecommendationService.getRecommendCourses(id, price, rating, duration);
        return ResponseEntity.ok().body(courses);
    }

}
