package openerp.openerpresourceserver.thesisdefensejuryassignment.models;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;
public class AssignTeacherToDefenseJuryAutomaticallyIM {
    private List<String> thesisIdList;

    public AssignTeacherToDefenseJuryAutomaticallyIM(String thesisDefensePlanId, String defenseJuryId, List<String> thesisIdList) {
        this.thesisIdList = thesisIdList;
    }

    public List<String> getThesisIdList() {
        return thesisIdList;
    }

    public void setThesisIdList(List<String> thesisIdList) {
        this.thesisIdList = thesisIdList;
    }
}
