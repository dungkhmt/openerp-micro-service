package openerp.openerpresourceserver.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.entity.Attendance;
import openerp.openerpresourceserver.repo.AttendanceRepository;
import openerp.openerpresourceserver.service.AttendanceService;
import org.bytedeco.opencv.opencv_core.IplImage;
import org.bytedeco.opencv.opencv_core.Mat;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static org.bytedeco.opencv.global.opencv_core.cvarrToMat;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final FaceRecognitionService faceRecognitionService;
    private Integer lastEmployeeId = null;
    private LocalDateTime lastAttendanceTime = null;

    @Override
    public Attendance recordAttendance(Mat face, HttpServletRequest request) {
        int employeeId = faceRecognitionService.recognize(face);

        if (employeeId == -1) {
            return null;
        }

        LocalDateTime now = LocalDateTime.now();
        int currentDate = Integer.parseInt(now.toLocalDate().toString().replace("-", ""));

        if (lastEmployeeId != null && lastEmployeeId.equals(employeeId) &&
                lastAttendanceTime != null && lastAttendanceTime.plusMinutes(1).isAfter(now)) {
            return null;
        }

        Attendance attendance = new Attendance();
        attendance.setId(employeeId);
        attendance.setTime(now);
        attendance.setDate(currentDate);
        attendance.setIp(getClientIp(request));

        lastEmployeeId = employeeId;
        lastAttendanceTime = now;

        return attendanceRepository.save(attendance);
    }

    private String getClientIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }

        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0].trim();
        }

        return ipAddress;
    }
}
