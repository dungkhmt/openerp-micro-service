package openerp.openerpresourceserver.thesisdefensejuryassignment.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ThesisDefensePlanDTO {

    private String id;

    private String name;

    private String description;

    private String semester;
    private Date startDate;

    private Date endDate;

    public ThesisDefensePlanDTO(String id, String name, String description, String semester, Date startDate, Date endDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.semester = semester;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
