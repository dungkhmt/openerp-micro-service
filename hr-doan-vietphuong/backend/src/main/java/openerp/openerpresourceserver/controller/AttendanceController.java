package openerp.openerpresourceserver.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.dto.response.Result;
import openerp.openerpresourceserver.entity.Attendance;
import openerp.openerpresourceserver.service.AttendanceService;
import openerp.openerpresourceserver.service.impl.FaceRecognitionService;
import org.bytedeco.opencv.opencv_core.IplImage;
import org.bytedeco.opencv.opencv_core.Mat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;

@RestController
@RequiredArgsConstructor
@RequestMapping("/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    private final FaceRecognitionService faceRecognitionService;

    @PostMapping("/test")
    public Result testing(@RequestParam("files") MultipartFile[] files) {
        List<Double> result = new ArrayList<>();
        try {
            for (MultipartFile file : files) {
                File tempFile = File.createTempFile("uploaded-", file.getOriginalFilename());
                file.transferTo(tempFile);
                Mat image = imread(tempFile.getAbsolutePath());

                faceRecognitionService.init();
                result.add(faceRecognitionService.recognizes(image));
                tempFile.delete();
            }
            return Result.ok(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/recognize")
    public Result recognizeFace(@RequestParam("file") MultipartFile file) {
        try {
                File tempFile = File.createTempFile("uploaded-", file.getOriginalFilename());
                file.transferTo(tempFile);
                Mat image = imread(tempFile.getAbsolutePath());
                faceRecognitionService.init();
                int employeeId = faceRecognitionService.recognize(image);
                tempFile.delete();
            return Result.ok(employeeId);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/record")
    public Result recordAttendance(@RequestParam("file") MultipartFile file,
                                   HttpServletRequest request) {
        try {
            File tempFile = File.createTempFile("uploaded-", file.getOriginalFilename());
            file.transferTo(tempFile);
            Mat image = imread(tempFile.getAbsolutePath());
            faceRecognitionService.init();
            Attendance attendance = attendanceService.recordAttendance(image, request);
            tempFile.delete();
            return Result.ok(attendance);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}