package openerp.openerpresourceserver.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class DeliveryAddress {
    private String street;
    private String city;
    private String state;
    private String zipCode;
} 