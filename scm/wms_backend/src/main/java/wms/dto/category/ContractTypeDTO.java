package wms.dto.category;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ContractTypeDTO {
    @NotBlank(message = "Truong name khong duoc bo trong")
    private String name;
    @NotNull(message = "Truong channel code khong duoc de trong")
    private String channelCode;
}
