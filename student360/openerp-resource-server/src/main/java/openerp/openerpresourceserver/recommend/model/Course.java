package openerp.openerpresourceserver.recommend.model;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Course {

    @Id
    private String id;
    private String title;
    private String url;
    private String subtitle;
    private Double rating;
    private String reviews;
    private Double hours;
    private String lectures;
    private String level;
    private String currentPrice;
    private String originalPrice;
    private Double basicTfIdfScore;
    private Double advanceTfIdfScore;

    public void setHours(String hoursStr) {
        if (hoursStr == null || hoursStr.isEmpty()) {
            this.hours = 1.0;
        } else {
            try {
                this.hours = Double.parseDouble(hoursStr);
            } catch (NumberFormatException e) {
                this.hours = 1.0; // Giá trị mặc định nếu có lỗi khi chuyển đổi
            }
        }
    }

}
