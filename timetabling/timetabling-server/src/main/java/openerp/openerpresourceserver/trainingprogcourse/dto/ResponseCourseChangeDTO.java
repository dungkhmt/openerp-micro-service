package openerp.openerpresourceserver.trainingprogcourse.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ResponseCourseChangeDTO {
    String id;
    String semester;

    public ResponseCourseChangeDTO(String key, String s) {
        this.id = key;
        this.semester = s;
    }
}
