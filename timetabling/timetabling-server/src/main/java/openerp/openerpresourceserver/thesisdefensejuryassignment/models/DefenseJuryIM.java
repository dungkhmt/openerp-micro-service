package openerp.openerpresourceserver.thesisdefensejuryassignment.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DefenseJuryIM {

    private String name;
    private int maxThesis;
    private Date defenseDate;

    private int defenseRoomId;

    private List<Integer> defenseSessionId;
    private int juryTopicId;

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

    public Date getDefenseDate() {
        return defenseDate;
    }

    public void setDefenseDate(Date defenseDate) {
        this.defenseDate = defenseDate;
    }

    public int getDefenseRoomId() {
        return defenseRoomId;
    }

    public void setDefenseRoomId(int defenseRoomId) {
        this.defenseRoomId = defenseRoomId;
    }

    public List<Integer> getDefenseSessionId() {
        return defenseSessionId;
    }

    public void setDefenseSessionId(List<Integer> defenseSessionId) {
        this.defenseSessionId = defenseSessionId;
    }

    public int getJuryTopicId() {
        return juryTopicId;
    }

    public void setJuryTopicId(int juryTopicId) {
        this.juryTopicId = juryTopicId;
    }
}
