package openerp.openerpresourceserver.thesisdefensejuryassignment.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UpdateDefenseJuryDTO extends Response {
    public UpdateDefenseJuryDTO(String message, int statusCode) {
        super(message, statusCode);
    }
}
