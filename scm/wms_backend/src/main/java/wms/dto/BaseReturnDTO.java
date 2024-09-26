package wms.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class BaseReturnDTO {
    private Long id;
    private ZonedDateTime createdDate;
    private ZonedDateTime updatedDate;
    private String uid;
}
