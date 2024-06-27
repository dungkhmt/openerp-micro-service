package openerp.openerpresourceserver.thesisdefensejuryassignment.models;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;
public class AssignTeacherToDefenseJuryAutomaticallyIM {
    private String thesisDefensePlanId;
    private String defenseJuryId;

    public AssignTeacherToDefenseJuryAutomaticallyIM(String defenseJuryId, String thesisDefensePlanId) {
        this.defenseJuryId  = defenseJuryId;
        this.thesisDefensePlanId = thesisDefensePlanId;
    }

    public String getDefenseJuryId() {
        return defenseJuryId;
    }

    public void setDefenseJuryId(String defenseJuryId) {
        this.defenseJuryId = defenseJuryId;
    }

    public String getThesisDefensePlanId() {
        return thesisDefensePlanId;
    }

    public void setThesisDefensePlanId(String thesisDefensePlanId) {
        this.thesisDefensePlanId = thesisDefensePlanId;
    }
}
