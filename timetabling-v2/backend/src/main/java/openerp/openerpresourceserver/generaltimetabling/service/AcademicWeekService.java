package openerp.openerpresourceserver.generaltimetabling.service;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.generaltimetabling.repo.AcademicWeekRepo;
import openerp.openerpresourceserver.generaltimetabling.model.entity.AcademicWeek;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class AcademicWeekService {
    private AcademicWeekRepo academicWeekRepo;
    public List<AcademicWeek> saveAcademicWeeks(String semester, String startDate, int numberOfWeeks) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        List<AcademicWeek> academicWeekList = new ArrayList<>();
        try {
            Calendar calendar = Calendar.getInstance();
            Date parsedStartDate = dateFormat.parse(startDate);
            calendar.setTime(parsedStartDate);
            for (int i = 0; i < numberOfWeeks; i++) {
                AcademicWeek academicWeek = new AcademicWeek();
                academicWeek.setStartDayOfWeek(dateFormat.format(calendar.getTime()));
                academicWeek.setWeekIndex(i+1);
                academicWeek.setSemester(semester);
                academicWeekList.add(academicWeek);
                calendar.add(Calendar.WEEK_OF_YEAR, 1);
            }
        } catch (ParseException e) {
            e.printStackTrace(); // Handle parsing exception if the date string format is incorrect
        }
        return academicWeekRepo.saveAll(academicWeekList);
    }
    public List<AcademicWeek> getAllWeeks(String semester) {
        return academicWeekRepo.findAllBySemester(semester);
    }
    @Transactional
    public void deleteAllBySemester(String semester) {
        academicWeekRepo.deleteBySemester(semester);
    }
}
