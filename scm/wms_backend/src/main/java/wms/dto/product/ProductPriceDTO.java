package wms.dto.product;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class ProductPriceDTO {
    private ZonedDateTime startedDate;
    private ZonedDateTime endedDate;
    private double priceBeforeVat;
    private double vat;
//    private String contractTypeCode;
    private String productCode;
    private String status;
}
