package openerp.openerpresourceserver.recommend.model;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    private String rating;
    private String reviews;
    private String hours;
    private String lectures;
    private String level;
    private String currentPrice;
    private String originalPrice;
}
