package openerp.openerpresourceserver.application.port.out.job_position.filter;

import openerp.openerpresourceserver.constant.JobPositionStatus;

public interface IJobPositionFilter {
    String getCode();
    String getName();
    JobPositionStatus getStatus();
}
