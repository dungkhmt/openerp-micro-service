package openerp.openerpresourceserver.dto;

import lombok.*;
import openerp.openerpresourceserver.entity.Hub;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HubGeneral {
    private UUID id;
    private String name;
    private String code;

    private Double width;
    private Double length;
    private String address;
    private Double longitude;
    private Double latitude;

    public HubGeneral(Hub hub) {
        this.id = hub == null ? null : hub.getHubId();
        this.name = hub == null ? null : hub.getName();
        this.width = hub == null ? null : hub.getWidth();
        this.length = hub == null ? null : hub.getLength();
        this.address = hub == null ? null : hub.getAddress();
        this.longitude = hub == null ? null : hub.getLongitude();
        this.latitude = hub == null ? null : hub.getLatitude();
    }
}
