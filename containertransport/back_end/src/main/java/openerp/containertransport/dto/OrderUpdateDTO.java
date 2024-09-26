package openerp.containertransport.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderUpdateDTO {
    private String status;
    private List<String> uidList;
}
