package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.model.entity.Schedule;
import openerp.openerpresourceserver.repo.ScheduleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import openerp.openerpresourceserver.helper.ExcelHelper;

@Service
public class ExcelService {
    @Autowired
    ScheduleRepo scheduleRepo;

    public ByteArrayInputStream load() {
        List<Schedule> schedules = this.getAllSchedules();
        ByteArrayInputStream in = ExcelHelper.schedulesToExcel(schedules);
        return in;
    }

    public ByteArrayInputStream loadExport() {
        List<Schedule> schedules = this.getAllSchedules();
        ByteArrayInputStream in = ExcelHelper.schedulesToExcelExport(schedules);
        return in;
    }

    public void save(MultipartFile file) {
        try {
            List<Schedule> tutorials = ExcelHelper.excelToSchedules(file.getInputStream());
            scheduleRepo.saveAll(tutorials);
        } catch (IOException e) {
            throw new RuntimeException("fail to store excel data: " + e.getMessage());
        }
    }
    public List<Schedule> getAllSchedules() {
        return scheduleRepo.findAll();
    }
}
