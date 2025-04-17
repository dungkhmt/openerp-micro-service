package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.dto.request.PagingRequest;
import openerp.openerpresourceserver.dto.request.attendanceRange.AttendanceRangeQueryRequest;
import openerp.openerpresourceserver.dto.request.attendanceRange.AttendanceRangeRequest;
import openerp.openerpresourceserver.entity.AttendanceRange;
import org.springframework.data.domain.Page;

public interface AttendanceRangeService {
    AttendanceRange createAttendanceRange(AttendanceRangeRequest request);

    AttendanceRange updateAttendanceRange(AttendanceRangeRequest request);

    Page<AttendanceRange> getAttendanceRanges(AttendanceRangeQueryRequest request, PagingRequest pagingRequest);

    AttendanceRange deleteAttendanceRange(long id);

    AttendanceRange getAttendanceRangeById(Long id);
}
