package openerp.openerpresourceserver.thesisdefensejuryassignment.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
public class DefenseSessionIM {
    private int defenseSessionId;

    public int getDefenseSessionId() {
        return defenseSessionId;
    }

    public void setDefenseSessionId(int defenseSessionId) {
        this.defenseSessionId = defenseSessionId;
    }
}
