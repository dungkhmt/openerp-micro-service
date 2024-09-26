package openerp.openerpresourceserver.thesisdefensejuryassignment.models;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class DefenseRoomIM {
    private int defenseRoomId;

    public int getDefenseRoomId() {
        return defenseRoomId;
    }

    public void setDefenseRoomId(int defenseRoomId) {
        this.defenseRoomId = defenseRoomId;
    }
}
