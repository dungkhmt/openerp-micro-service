package openerp.containertransport.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class OrderFilterRequestDTO implements Serializable {
    private String orderCode;
    private List<String> status;
    private Integer page;
    private Integer pageSize;
    private String owner;
    private String type;
}
