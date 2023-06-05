package openerp.containertransport.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TypeContainerFilterReqDTO {
    private Integer page;
    private Integer pageSize;
}
