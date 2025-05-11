package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.dto.request.attendance.AttendanceRequest;
import org.springframework.core.io.InputStreamResource;

public interface ExportService {
    InputStreamResource exportReport(AttendanceRequest request);
}
