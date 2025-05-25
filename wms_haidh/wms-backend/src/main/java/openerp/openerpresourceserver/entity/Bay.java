package openerp.openerpresourceserver.entity;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "wms_bay")
public class Bay {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID bayId;

    private UUID warehouseId;

    private String code;

    private int x;

    private int y;

    private int xLong; // in meters

    private int yLong; // in meters
    
    private int shelf;

}

