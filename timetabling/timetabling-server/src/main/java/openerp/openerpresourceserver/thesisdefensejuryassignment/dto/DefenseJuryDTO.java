package openerp.openerpresourceserver.thesisdefensejuryassignment.dto;

import lombok.Data;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.DefenseRoom;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.DefenseSession;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.embedded.DefenseJuryTeacherRole;

import java.util.Date;
import java.util.List;

@Data
public class DefenseJuryDTO {
    String id;
    Date defenseDate;
    String name;
    int maxThesis;
    DefenseRoom defenseRoom;
    List<DefenseSession> defenseSession;
    List<ThesisDTO> thesisList;
    List<DefenseJuryTeacherRole> defenseJuryTeacherRoles;
    boolean assigned;

    JuryTopicDTO juryTopic;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDefenseDate() {
        return defenseDate;
    }

    public void setDefenseDate(Date defenseDate) {
        this.defenseDate = defenseDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMaxThesis() {
        return maxThesis;
    }

    public void setMaxThesis(int maxThesis) {
        this.maxThesis = maxThesis;
    }

    public DefenseRoom getDefenseRoom() {
        return defenseRoom;
    }

    public void setDefenseRoom(DefenseRoom defenseRoom) {
        this.defenseRoom = defenseRoom;
    }

    public List<DefenseSession> getDefenseSession() {
        return defenseSession;
    }

    public void setDefenseSession(List<DefenseSession> defenseSession) {
        this.defenseSession = defenseSession;
    }

    public List<ThesisDTO> getThesisList() {
        return thesisList;
    }

    public void setThesisList(List<ThesisDTO> thesisList) {
        this.thesisList = thesisList;
    }

    public List<DefenseJuryTeacherRole> getDefenseJuryTeacherRoles() {
        return defenseJuryTeacherRoles;
    }

    public void setDefenseJuryTeacherRoles(List<DefenseJuryTeacherRole> defenseJuryTeacherRoles) {
        this.defenseJuryTeacherRoles = defenseJuryTeacherRoles;
    }

    public boolean isAssigned() {
        return assigned;
    }

    public void setAssigned(boolean assigned) {
        this.assigned = assigned;
    }

    public JuryTopicDTO getJuryTopicDTO() {
        return juryTopic;
    }

    public void setJuryTopicDTO(JuryTopicDTO juryTopicDTO) {
        this.juryTopic = juryTopicDTO;
    }
}
