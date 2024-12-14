package openerp.openerpresourceserver.labtimetabling.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
@Data
@NoArgsConstructor
public class AssignDTOUpdateRequest {
    @JsonProperty("update")
    private Set<AssignDTO> updateSet;
    @JsonProperty("remove")
    private Set<AssignDTO> removeSet;
}
