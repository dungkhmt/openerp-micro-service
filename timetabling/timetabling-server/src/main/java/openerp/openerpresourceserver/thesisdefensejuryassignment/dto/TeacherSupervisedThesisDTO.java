package openerp.openerpresourceserver.thesisdefensejuryassignment.dto;

import lombok.Data;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.Thesis;

import java.util.List;
@Data
public class TeacherSupervisedThesisDTO {
    public String thesisDefensePlan;

    public List<Thesis> thesisList;

    public TeacherSupervisedThesisDTO(String thesisDefensePlan, List<Thesis> thesisList) {
        this.thesisDefensePlan = thesisDefensePlan;
        this.thesisList = thesisList;
    }

    public String getThesisDefensePlan() {
        return thesisDefensePlan;
    }
    public void setThesisDefensePlan(String thesisDefensePlan) {
        this.thesisDefensePlan = thesisDefensePlan;
    }

    public List<Thesis> getThesisList() {
        return thesisList;
    }

    public void setThesisList(List<Thesis> thesisList) {
        this.thesisList = thesisList;
    }
}
