package com.hust.openerp.taskmanagement.hr_management.application.port.out.job_position.usecase_data;

import com.hust.openerp.taskmanagement.hr_management.constant.JobPositionType;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.job_position.filter.IJobPositionFilter;
import com.hust.openerp.taskmanagement.hr_management.constant.JobPositionStatus;
import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;
import com.hust.openerp.taskmanagement.hr_management.domain.model.IPageableRequest;

@Data
@Builder
@Getter
@Setter
public class GetJobPosition implements IJobPositionFilter, UseCase {
    private String code;
    private String name;
    private JobPositionStatus status;
    private JobPositionType type;
    private IPageableRequest pageableRequest;
}
