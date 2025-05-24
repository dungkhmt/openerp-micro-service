package com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_configure.usecase_data;

import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Builder
@Getter
@Setter
public class GetCheckpointConfigureIn implements UseCase {
    private List<String> configureCodes;
}
