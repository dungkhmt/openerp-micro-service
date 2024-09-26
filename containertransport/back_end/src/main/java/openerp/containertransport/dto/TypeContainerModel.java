package openerp.containertransport.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TypeContainerModel implements Serializable {
    private long id;
    private String typeContainerCode;
    private Integer size;
    private Integer total;
    private long createdAt;
    private long updatedAt;
}
