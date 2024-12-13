package openerp.openerpresourceserver.tarecruitment.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.tarecruitment.entity.dto.ChartDTO;
import openerp.openerpresourceserver.tarecruitment.entity.dto.PaginationDTO;
import openerp.openerpresourceserver.tarecruitment.entity.ClassCall;
import openerp.openerpresourceserver.tarecruitment.repo.ClassCallRepo;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
@AllArgsConstructor(onConstructor_ = @Autowired)
@Service
public class ClassCallServiceImpl implements ClassCallService {

    private ClassCallRepo classCallRepo;
    private TARecruitment_SemesterService semesterService;
    @Override
    public ClassCall createNewClass(ClassCall classCall) {
        Optional<ClassCall> existClassCall = classCallRepo.findById(classCall.getId());
        if(!existClassCall.isEmpty()) throw new IllegalArgumentException("Mã lớp " + classCall.getId() + " đã tồn tại");
        classCallRepo.save(classCall);
        return classCall;
    }

    @Override
    public PaginationDTO<ClassCall> getAllClass(int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        Page<ClassCall> classCallsPage = classCallRepo.findAll(pageable);

        PaginationDTO<ClassCall> paginationDTO = new PaginationDTO<>();
        paginationDTO.setPage(classCallsPage.getNumber());
        paginationDTO.setTotalElement((int) classCallsPage.getTotalElements());
        paginationDTO.setData(classCallsPage.getContent());

        return paginationDTO;
    }

    @Override
    public Optional<ClassCall> getClassById(int id) {
        Optional<ClassCall> classCall = classCallRepo.findById(id);
        if(classCall.isEmpty()) throw new IllegalArgumentException("Class with ID " + id + " not found");
        else return classCall;
    }

    @Override
    public PaginationDTO<ClassCall> getClassBySemester(String semester, String search, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        Page<ClassCall> classCallsPage = classCallRepo.findBySemester(semester, search, pageable);

        PaginationDTO<ClassCall> paginationDTO = new PaginationDTO<>();

        paginationDTO.setPage(classCallsPage.getNumber());
        paginationDTO.setTotalElement((int) classCallsPage.getTotalElements());
        paginationDTO.setData(classCallsPage.getContent());

        return paginationDTO;
    }

    @Override
    public ClassCall updateClass(int id, ClassCall classCall) {
        Optional<ClassCall> existingClassCallOptional = classCallRepo.findById(id);
        if (existingClassCallOptional.isEmpty()) {
            throw new IllegalArgumentException("Class with ID " + id + " not found");
        }

        ClassCall existingClassCall = existingClassCallOptional.get();
        existingClassCall.setId(classCall.getId());
        existingClassCall.setDay(classCall.getDay());
        existingClassCall.setStartPeriod(classCall.getStartPeriod());
        existingClassCall.setEndPeriod(classCall.getEndPeriod());
        existingClassCall.setSubjectId(classCall.getSubjectId());
        existingClassCall.setSubjectName(classCall.getSubjectName());
        existingClassCall.setClassRoom(classCall.getClassRoom());
        existingClassCall.setNote(classCall.getNote());
        existingClassCall.setSemester(classCall.getSemester());

        // Save the updated class call to the database
        ClassCall updatedClassCall = classCallRepo.save(existingClassCall);

        return updatedClassCall;
    }

    @Override
    public boolean deleteClass(int id) {
        Optional<ClassCall> existingClassCallOptional = classCallRepo.findById(id);
        if (existingClassCallOptional.isEmpty()) {
            throw new IllegalArgumentException("Class with ID " + id + " not found");
        }

        classCallRepo.deleteById(id);
        return true;
    }

    @Override
    public boolean deleteMultipleClass(List<Integer> idList) {
        for(int id : idList) {
            classCallRepo.deleteById(id);
        }
        return true;
    }

    @Override
    public List<ClassCall> getAllMyRegisteredClass(String userId, String semester) {
        return classCallRepo.getAllMyRegisteredClass(userId, semester);
    }

    @Override
    public int importClass(MultipartFile file, String semester) {
        try (InputStream inputStream = file.getInputStream()) {
            int numberOfData = 0;
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            Row firstRow = sheet.getRow(0);
            log.info(firstRow);
            log.info("Did it get at here");
            if(firstRow != null) {
                Cell idCell = firstRow.getCell(0);
                Cell classIdCell = firstRow.getCell(1);
                Cell subjectCell = firstRow.getCell(2);
                Cell room = firstRow.getCell(3);
                Cell day = firstRow.getCell(4);
                Cell startPeriod = firstRow.getCell(5);
                Cell endPeriod = firstRow.getCell(6);
                Cell note = firstRow.getCell(7);


                // Check if excel is valid
                if("Mã lớp".equals(idCell.getStringCellValue()) &&
                        "Mã môn học".equals(classIdCell.getStringCellValue()) &&
                        "Tên môn học".equals(subjectCell.getStringCellValue()) &&
                        "Phòng học".equals(room.getStringCellValue()) &&
                        "Ngày".equals(day.getStringCellValue()) &&
                        "Tiết bắt đầu".equals(startPeriod.getStringCellValue()) &&
                        "Tiết kết thúc".equals(endPeriod.getStringCellValue()) &&
                        "Ghi chú".equals(note.getStringCellValue())) {
                    log.info("The excel file is valid");
                    int dataRowNums = 1;
                    Row dataRow = sheet.getRow(dataRowNums);
                    while(dataRow != null) {

                        int classId = (int) dataRow.getCell(0).getNumericCellValue();
                        log.info("Class Id " + classId);
                        String subjectId = dataRow.getCell(1).getStringCellValue();
                        log.info("Subject Id " + subjectId);
                        String subjectName = dataRow.getCell(2).getStringCellValue();
                        log.info("Subject Name " + subjectName);
                        String subjectRoom = dataRow.getCell(3).getStringCellValue();
                        log.info("Subject Room " + subjectRoom);
                        int classDay = (int) dataRow.getCell(4).getNumericCellValue();
                        log.info("Day " + classDay);
                        int classStartPeriod = (int) dataRow.getCell(5).getNumericCellValue();
                        log.info("Start period " + classStartPeriod);
                        int classEndPeriod = (int) dataRow.getCell(6).getNumericCellValue();
                        log.info("End period " + classEndPeriod);
                        Cell classNote = dataRow.getCell(7);
                        String classNoteData = "";
                        if(!(classNote == null || classNote.getCellType() == Cell.CELL_TYPE_BLANK)) {
                            classNoteData = classNote.getStringCellValue();
                        }
                        log.info("Note " + classNoteData);

                        Optional<ClassCall> existClassCall = classCallRepo.findById(classId);

                        if(existClassCall.isEmpty()) {
                            ClassCall newClass = new ClassCall();
                            newClass.setId(classId);
                            newClass.setSubjectId(subjectId);
                            newClass.setSubjectName(subjectName);
                            newClass.setClassRoom(subjectRoom);
                            newClass.setSemester(semester);
                            newClass.setDay(classDay);
                            newClass.setStartPeriod(classStartPeriod);
                            newClass.setEndPeriod(classEndPeriod);
                            newClass.setNote(classNoteData);

                            classCallRepo.save(newClass);
                            numberOfData++;
                        }

                        dataRowNums++;
                        dataRow = sheet.getRow(dataRowNums);
                    }
                    return numberOfData;
                }
                else {
                    throw new IllegalArgumentException("Excel file is invalid");
                }
            }
        } catch(IOException e) {
            throw new IllegalArgumentException("Something is wrong");
        }
        return 0;
    }

    @Override
    public List<ChartDTO> getNumberOfClassEachSemester() {
        List<ChartDTO> chart = new ArrayList<>();
        List<String> semesters = semesterService.getAllSemester();
        for(String semester : semesters) {
            List<ClassCall> classcalls = classCallRepo.findAllBySemester(semester);
            ChartDTO data = new ChartDTO(semester, classcalls.size());
            chart.add(data);
        }
        return chart;
    }
}
