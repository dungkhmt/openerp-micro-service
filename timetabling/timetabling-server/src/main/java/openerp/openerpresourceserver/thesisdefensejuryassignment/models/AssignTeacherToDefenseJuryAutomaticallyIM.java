package openerp.openerpresourceserver.thesisdefensejuryassignment.models;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;
public class AssignTeacherToDefenseJuryAutomaticallyIM {
    String thesisDefensePlanId;
    List<DefenseJuryTeacherRoleIM> defenseJuryTeacherRole;

    public AssignTeacherToDefenseJuryAutomaticallyIM(String thesisDefensePlanId, List<DefenseJuryTeacherRoleIM> defenseJuryTeacherRole) {
        this.thesisDefensePlanId = thesisDefensePlanId;
        this.defenseJuryTeacherRole = defenseJuryTeacherRole;
    }

    public String getThesisDefensePlanId() {
        return thesisDefensePlanId;
    }

    public void setThesisDefensePlanId(String thesisDefensePlanId) {
        this.thesisDefensePlanId = thesisDefensePlanId;
    }

    public List<DefenseJuryTeacherRoleIM> getDefenseJuryTeacherRole() {
        return defenseJuryTeacherRole;
    }

    public void setDefenseJuryTeacherRole(List<DefenseJuryTeacherRoleIM> defenseJuryTeacherRole) {
        this.defenseJuryTeacherRole = defenseJuryTeacherRole;
    }
}
