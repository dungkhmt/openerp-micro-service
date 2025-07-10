package openerp.openerpresourceserver.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import openerp.openerpresourceserver.entity.enumentity.HubType;

import java.util.List;

@Data
@ToString
@Builder
public class HubWithBaysDto {

    private String id;
    @NotBlank
    private String address;
    @NotBlank
    private String code;

    private HubType hubType;
    @NotBlank
    private String name;
    @Min(value = 0)
    private Double length;
    @Min(value = 0)
    private Double width;
    private Double longitude;
    private Double latitude;
    @Valid
    private List<Shelf> listShelf;

    // Default constructor
    public HubWithBaysDto() {
    }

    // Constructor with all fields
    public HubWithBaysDto(String id, @NotBlank String address, @NotBlank String code,
                          @NotNull HubType hubType, @NotBlank String name, @Min(value = 0) Double length,
                          @Min(value = 0) Double width, double longitude, double latitude,
                          @Valid List<Shelf> listShelf) {
        if(id != null) this.id = id;
        this.address = address;
        this.code = code;
        this.hubType = hubType;
        this.name = name;
        this.length = length;
        this.width = width;
        this.longitude = longitude;
        this.latitude = latitude;
        this.listShelf = listShelf;
    }



    @Data
    @AllArgsConstructor
    @Builder
    public static class Shelf {
        private String id;
        @NotBlank
        private String code;
        @Min(value = 0)
        private Integer x;
        @Min(value = 0)
        private Integer y;
        @Min(value = 0)
        private Integer width;
        @Min(value = 0)
        private Integer length;
        private boolean canBeDelete;
    }
}