package openerp.openerpresourceserver.thesisdefensejuryassignment.models;

import java.util.List;

public class AssignTeacherAndThesisToDefenseJuryIM {
    private List<DefenseJuryTeacherRoleIM> defenseJuryTeacherRole;

    private List<String> thesisIdList;

    private String defenseJuryId;

    public List<DefenseJuryTeacherRoleIM> getDefenseJuryTeacherRole() {
        return defenseJuryTeacherRole;
    }

    public void setDefenseJuryTeacherRole(List<DefenseJuryTeacherRoleIM> defenseJuryTeacherRole) {
        this.defenseJuryTeacherRole = defenseJuryTeacherRole;
    }

    public List<String> getThesisIdList() {
        return thesisIdList;
    }

    public void setThesisIdList(List<String> thesisIdList) {
        this.thesisIdList = thesisIdList;
    }

    public String getDefenseJuryId() {
        return defenseJuryId;
    }

    public void setDefenseJuryId(String defenseJuryId) {
        this.defenseJuryId = defenseJuryId;
    }
}
