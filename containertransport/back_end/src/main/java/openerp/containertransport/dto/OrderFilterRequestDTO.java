package openerp.containertransport.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderFilterRequestDTO implements Serializable {
    private String orderCode;
    private String status;
    private Integer page;
    private Integer pageSize;
    private String owner;
}
