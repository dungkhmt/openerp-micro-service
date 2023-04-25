package openerp.containertransport.dto;

import lombok.Data;

@Data
public class OrderFilterRequestDTO {
    private String orderCode;
    private String status;
}
