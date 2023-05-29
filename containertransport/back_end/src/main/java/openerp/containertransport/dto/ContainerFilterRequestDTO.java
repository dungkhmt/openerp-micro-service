package openerp.containertransport.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContainerFilterRequestDTO {
    private String containerCode;
    private Integer page;
    private Integer pageSize;
}
