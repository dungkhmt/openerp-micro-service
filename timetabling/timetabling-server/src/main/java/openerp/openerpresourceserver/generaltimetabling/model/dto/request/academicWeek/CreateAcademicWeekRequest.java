package openerp.openerpresourceserver.generaltimetabling.model.dto.request.academicWeek;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.sql.Date;


@AllArgsConstructor
@Getter
public class CreateAcademicWeekRequest {
    private String semester;
    private String startDate;
    private int numberOfWeeks;
}
