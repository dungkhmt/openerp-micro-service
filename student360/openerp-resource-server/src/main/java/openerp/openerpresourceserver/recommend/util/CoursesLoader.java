package openerp.openerpresourceserver.recommend.util;

import openerp.openerpresourceserver.recommend.model.Course;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CoursesLoader {
    public static List<Course> loadCoursesFromCSV(String csvFileName) {
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
                course.setRating(Double.valueOf(csvRecord.get("rating")));
                course.setReviews(csvRecord.get("reviews"));
                course.setHours(csvRecord.get("hours"));
                course.setLectures(csvRecord.get("lectures"));
                course.setLevel(csvRecord.get("level"));
                course.setCurrentPrice(csvRecord.get("currentPrice"));
                course.setOriginalPrice(csvRecord.get("originalPrice"));
                course.setBasicTfIdfScore(Double.valueOf(csvRecord.get("tf-idf-for-basic-student")));
                course.setAdvanceTfIdfScore(Double.valueOf(csvRecord.get("tf-idf-for-advanced-student")));
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
