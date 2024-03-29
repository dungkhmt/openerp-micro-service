package openerp.openerpresourceserver.dto;

import lombok.Data;

import java.util.Date;

@Data
public class OrderDTO {

    private String customerId;

    private Double weight;

    private Double volume;

    private Date fromDateTime;

    private Date toDateTime;

    private String address;

    private String latitude;

    private String longitude;

    private String status;
}
