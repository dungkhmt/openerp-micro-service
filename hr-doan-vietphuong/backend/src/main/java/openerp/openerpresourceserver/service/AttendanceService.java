package openerp.openerpresourceserver.service;

import jakarta.servlet.http.HttpServletRequest;
import openerp.openerpresourceserver.entity.Attendance;
import org.bytedeco.opencv.opencv_core.IplImage;
import org.bytedeco.opencv.opencv_core.Mat;

import java.util.List;

public interface AttendanceService {

    Attendance recordAttendance(Mat face, HttpServletRequest request);
}