package openerp.openerpresourceserver.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.dto.request.attendance.AttendanceRequest;
import openerp.openerpresourceserver.service.ExportService;
import openerp.openerpresourceserver.util.DateUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/report")
public class AdminExportController {
    private final ExportService exportService;

    @GetMapping("/export")
    public ResponseEntity<?> exportReport(@Valid AttendanceRequest request) throws IOException {
        String fileName = "BẢNG CHẤM CÔNG THÁNG " + DateUtil.getMonthByDate(request.getEnd()) + " NĂM " + request.getEnd().getYear() + ".xlsx";
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + encodedFileName)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(exportService.exportReport(request));
    }
}
