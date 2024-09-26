package thesisdefensejuryassignment.thesisdefenseserver.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Data
public class ThesisDTO {
    private UUID id;
    private String thesisName;
    private String thesisAbstract;
    private String supervisor;
    private String thesisDefensePlanId; // *
    private UUID defenseJuryId;

    private String defenseJuryName;

    private String scheduledReviewer;

    public String getDefenseJuryName() {
        return defenseJuryName;
    }

    public void setDefenseJuryName(String defenseJuryName) {
        this.defenseJuryName = defenseJuryName;
    }

    public String getScheduledReviewer() {
        return scheduledReviewer;
    }

    public void setScheduledReviewer(String scheduledReviewer) {
        this.scheduledReviewer = scheduledReviewer;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getThesisName() {
        return thesisName;
    }

    public void setThesisName(String thesisName) {
        this.thesisName = thesisName;
    }

    public String getThesisAbstract() {
        return thesisAbstract;
    }

    public void setThesisAbstract(String thesisAbstract) {
        this.thesisAbstract = thesisAbstract;
    }


    public String getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(String supervisor) {
        this.supervisor = supervisor;
    }

    public String getThesisDefensePlanId() {
        return thesisDefensePlanId;
    }

    public void setThesisDefensePlanId(String thesisDefensePlanId) {
        this.thesisDefensePlanId = thesisDefensePlanId;
    }

    public UUID getDefenseJuryId() {
        return defenseJuryId;
    }

    public void setDefenseJuryId(UUID defenseJuryId) {
        this.defenseJuryId = defenseJuryId;
    }
}
