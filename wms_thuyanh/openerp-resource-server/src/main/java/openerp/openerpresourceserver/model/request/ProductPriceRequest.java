package openerp.openerpresourceserver.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductPriceRequest {
    @NotBlank
    private String productId;
    @NotNull
    private BigDecimal price;
    private Date startDate;
    private Date endDate;
    private String description;
}
