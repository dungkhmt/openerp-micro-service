package openerp.openerpresourceserver.tarecruitment.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PaginationDTO<T> {

    private int page;
    private int totalElement;
    private List<T> data;

}
