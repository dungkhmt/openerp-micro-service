package openerp.openerpresourceserver.thesisdefensejuryassignment.models;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

public class DefenseJuryTeacherRoleIM {
    private String teacherName;
    private int roleId;

    public DefenseJuryTeacherRoleIM(String teacherName, int roleId) {
        this.teacherName = teacherName;
        this.roleId =  roleId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }
}
