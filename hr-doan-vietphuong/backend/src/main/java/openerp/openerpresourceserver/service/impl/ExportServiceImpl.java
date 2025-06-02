package openerp.openerpresourceserver.service.impl;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.dto.request.attendance.AttendanceRequest;
import openerp.openerpresourceserver.entity.*;
import openerp.openerpresourceserver.enums.*;
import openerp.openerpresourceserver.exception.InternalServerException;
import openerp.openerpresourceserver.repo.*;
import openerp.openerpresourceserver.service.*;
import openerp.openerpresourceserver.util.DateUtil;
import openerp.openerpresourceserver.util.ExcelUtil;
import openerp.openerpresourceserver.util.ObjectUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static openerp.openerpresourceserver.util.Constants.*;

@Service
@RequiredArgsConstructor
public class ExportServiceImpl implements ExportService {
    private final AttendanceRangeService attendanceRangeService;
    private final AttendanceReportRepository attendanceReportRepository;
    private final EmployeeRepository employeeRepository;
    private final AbsenceRepository absenceRepository;
    private final SettingService settingService;
    private final AbsenceTypeRepository absenceTypeRepository;
    private final HolidayService holidayService;
    private static final int START_OF_DAY_COLUMN = 3;
    private static final int TOTAL_TEMPLATE_COLUMNS = 10;
    private static final int START_OF_NAME_ROW = 3;
    private int numberOfDays;
    private int endOfDayColumn;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<Integer> dates;
    private String month;
    private String year;
    private List<Integer> holidays;
    private static final double FULL_HOURS = 8.0;

    @Override
    public InputStreamResource exportReport(AttendanceRequest request) {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("templates/" + REPORT_TEMPLATE);
        if (inputStream == null) {
            throw new IllegalArgumentException("File not found: " + REPORT_TEMPLATE);
        }
        Workbook workbook;

        // initialize variable
        startDate = request.getStart();
        endDate = request.getEnd();
        holidays = holidayService.getAttendanceHoliday(startDate, endDate);
        month = DateUtil.getMonthByDate(endDate);
        year = String.valueOf(endDate.getYear());
        dates = DateUtil.getDatesInDateRange(startDate, endDate);
        numberOfDays = (int) ChronoUnit.DAYS.between(startDate, endDate.plusDays(1));
        endOfDayColumn = numberOfDays + START_OF_DAY_COLUMN;

        // fetch data
        List<Employee> employees = employeeRepository.findAllByStatus(StatusEnum.ACTIVE.ordinal());
        List<AttendanceReport> attendanceReports = attendanceReportRepository.findByDateBetween(
                DateUtil.convertLocalDateToInteger(startDate),
                DateUtil.convertLocalDateToInteger(endDate)
        );

        // manipulate data
        // Get absence type
        List<AbsenceType> absenceTypes = absenceTypeRepository.findByStatus(
                StatusEnum.ACTIVE.ordinal());
        Map<Long, AbsenceType> absenceTypeMap = absenceTypes.stream().collect(Collectors.toMap(
                AbsenceType::getId, absence -> absence
        ));
        Map<String, String> absenceDescriptionMap = absenceTypes.stream().collect(Collectors.toMap(
                AbsenceType::getCode, AbsenceType::getDescription
        ));

        // create attendance report map
        // key : user and date
        // value: attendance time and status
        Map<AttendanceMapKey, AttendanceMapValue> attendanceTimes = attendanceReports.stream().collect(Collectors.toMap(
                report -> AttendanceMapKey.builder()
                        .employeeId(report.getEmployeeId())
                        .date(report.getDate())
                        .build(),
                report -> AttendanceMapValue.builder()
                        .startTime(report.getStartTime())
                        .endTime(report.getEndTime())
                        .attendanceTime(report.getAttendanceTime())
                        .leaveTime(report.getLeaveTime())
                        .attendanceStatus(report.getStatus())
                        .build()
        ));

        // Get absence values
        List<Absence> absenceWithValues = absenceRepository.getAbsences(
                LocalDateTime.of(startDate, LocalTime.of(0, 0)),
                LocalDateTime.of(endDate, LocalTime.of(23, 59, 59)),
                AbsenceTypeEnum.ABSENCE.ordinal(),
                AbsenceStatus.APPROVED.ordinal(),
                true);
        Map<Integer, Double> absenceWithValueMap = absenceWithValues.stream()
                .collect(Collectors.groupingBy(
                        absence -> absence.getEmployee().getEmployeeId(),
                        Collectors.summingDouble(Absence::getLength)
                ));

        // Get absence no values
        List<Absence> absenceWithoutValues = absenceRepository.getAbsences(
                LocalDateTime.of(startDate, LocalTime.of(0, 0)),
                LocalDateTime.of(endDate, LocalTime.of(23, 59, 59)),
                AbsenceTypeEnum.ABSENCE.ordinal(),
                AbsenceStatus.APPROVED.ordinal(),
                false);
        Map<Integer, Double> absenceWithoutValueMap = absenceWithoutValues.stream()
                .collect(Collectors.groupingBy(
                        absence -> absence.getEmployee().getEmployeeId(),
                        Collectors.summingDouble(Absence::getLength)
                ));
        // manipulate workbook
        try {
            workbook = new XSSFWorkbook(inputStream);
            Font fontBlack9InSize = ExcelUtil.createBasicFont(9, false, IndexedColors.BLACK, workbook);
            Font boldFontBlack9InSize = ExcelUtil.createBasicFont(9, true, IndexedColors.BLACK, workbook);
            Font font12InSize = ExcelUtil.createBasicFont(12, true, IndexedColors.BLACK, workbook);
            CellStyle blackFontBlackBorderLeftCenterCellStyle = ExcelUtil.createBorderAlignedCellStyle(
                    fontBlack9InSize,
                    IndexedColors.BLACK,
                    HorizontalAlignment.LEFT,
                    VerticalAlignment.CENTER,
                    workbook);
            CellStyle blackFontBlackBorderCenterCellStyle = ExcelUtil.createBorderAlignedCellStyle(
                    fontBlack9InSize,
                    IndexedColors.BLACK,
                    HorizontalAlignment.CENTER,
                    VerticalAlignment.CENTER,
                    workbook);
            CellStyle boldBlackFontBlackBorderCenterCellStyle = ExcelUtil.createBorderAlignedCellStyle(
                    boldFontBlack9InSize,
                    IndexedColors.BLACK,
                    HorizontalAlignment.CENTER,
                    VerticalAlignment.CENTER,
                    workbook);
            Sheet sheet = workbook.getSheet("REPORT");
            // shift template column according to the days
            sheet.shiftColumns(START_OF_DAY_COLUMN, TOTAL_TEMPLATE_COLUMNS, numberOfDays);
            // format headers
            // format main title
            Map<String, CellStyle> holidayStyleMap = populateHolidayStyleMap(fontBlack9InSize, workbook);
            Map<String, CellStyle> noteStyleMap = populateNoteStyleMap(fontBlack9InSize, workbook);
            formatHeader(noteStyleMap, absenceDescriptionMap, fontBlack9InSize, font12InSize, sheet, workbook);
            // format days cell
            formatDayCell(font12InSize, sheet, workbook);
            // format border
            formatBorder(employees, sheet, workbook);

            // insert data
            for (int i = 0; i < employees.size(); i++) {
                Employee employee = employees.get(i);
                // insert userId
                ExcelUtil.setCellValue(START_OF_NAME_ROW + i, 0, employee.getEmployeeId(), blackFontBlackBorderLeftCenterCellStyle, sheet);
                // insert fullname
                ExcelUtil.setCellValue(START_OF_NAME_ROW + i, 1, employee.getFullName(), blackFontBlackBorderLeftCenterCellStyle, sheet);
                // insert organization name
                ExcelUtil.setCellValue(START_OF_NAME_ROW + i, 2, employee.getOrganization().getName(), blackFontBlackBorderLeftCenterCellStyle, sheet);

                Row row = sheet.getRow(START_OF_NAME_ROW + i);
                Integer employeeId = employee.getEmployeeId();

                AttendanceRange attendanceRange = employee.getAttendanceRange();
                // code is MCC
                if (MCC.equals(attendanceRange.getCode())) {
                    for (int j = START_OF_DAY_COLUMN; j < numberOfDays + START_OF_DAY_COLUMN; j++) {
                        Cell attendanceTimeCell = ExcelUtil.getCell(row, j, blackFontBlackBorderCenterCellStyle);
                        Integer date = dates.get(j - START_OF_DAY_COLUMN);
                        if (DateUtil.isWeekend(date)) continue;
                        if (holidays.contains(date)) {
                            attendanceTimeCell.setCellStyle(holidayStyleMap.get(HOLIDAY));
                        } else attendanceTimeCell.setCellValue(attendanceRange.getFullHours());
                    }
                } else {
                    populateNormalAttendanceTime(
                            row,
                            blackFontBlackBorderCenterCellStyle,
                            attendanceTimes,
                            absenceTypeMap,
                            noteStyleMap,
                            holidayStyleMap,
                            employeeId,
                            attendanceRange);
                }

                // insert total attendance time
                Cell totalAttendanceTime = ExcelUtil.getCell(row, endOfDayColumn);
                Cell fromCell = ExcelUtil.getCell(row, START_OF_DAY_COLUMN);
                Cell toCell = ExcelUtil.getCell(row, endOfDayColumn - 1);
                totalAttendanceTime.setCellStyle(boldBlackFontBlackBorderCenterCellStyle);
                totalAttendanceTime.setCellFormula(ExcelUtil.getCellSumFormula(fromCell, toCell));

                // insert total attendance days
                Cell totalAttendanceDays = ExcelUtil.getCell(row, endOfDayColumn + 1);
                totalAttendanceDays.setCellFormula(
                        ExcelUtil.getCellRoundFormula(totalAttendanceTime, FULL_HOURS)
                );
                totalAttendanceDays.setCellStyle(boldBlackFontBlackBorderCenterCellStyle);

                // insert total absence days
                Cell totalAbsenceWithValueDays = ExcelUtil.getCell(row, endOfDayColumn + 2);
                Double absenceWithValueDays = absenceWithValueMap.get(employee.getEmployeeId());
                totalAbsenceWithValueDays.setCellStyle(boldBlackFontBlackBorderCenterCellStyle);
                if (absenceWithValueDays != null) {
                    totalAbsenceWithValueDays.setCellValue(
                            absenceWithValueDays
                    );
                }

                Cell totalAbsenceWithoutValueDays = ExcelUtil.getCell(row, endOfDayColumn + 4);
                Double absenceWithoutValueDays = absenceWithoutValueMap.get(employee.getEmployeeId());
                totalAbsenceWithoutValueDays.setCellStyle(boldBlackFontBlackBorderCenterCellStyle);
                if (absenceWithoutValueDays != null) {
                    totalAbsenceWithoutValueDays.setCellValue(
                            absenceWithoutValueDays
                    );
                }

                // insert holidays
                if (holidays.size() != 0) {
                    Cell holiday = ExcelUtil.getCell(row, endOfDayColumn + 3);
                    holiday.setCellStyle(boldBlackFontBlackBorderCenterCellStyle);
                    holiday.setCellValue(holidays.size());
                }

                // insert total salary days
                Cell holidayCell = ExcelUtil.getCell(row, endOfDayColumn + 3);
                Cell totalSalaryDays = ExcelUtil.getCell(row, endOfDayColumn + 5);
                totalSalaryDays.setCellFormula(
                        ExcelUtil.getCellRoundSumFormula(totalAttendanceDays, holidayCell, 1)
                );
                totalSalaryDays.setCellStyle(boldBlackFontBlackBorderCenterCellStyle);

                // insert left absence days
                Cell leftAbsenceDays = ExcelUtil.getCell(row, endOfDayColumn + 6);
                leftAbsenceDays.setCellFormula(employee.getAnnualLeave().toString());
                leftAbsenceDays.setCellStyle(boldBlackFontBlackBorderCenterCellStyle);
            }

            ByteArrayOutputStream reportOut = new ByteArrayOutputStream();
            workbook.write(reportOut);
            ByteArrayInputStream in = new ByteArrayInputStream(reportOut.toByteArray());
            return new InputStreamResource(in);
        } catch (IOException e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    private void formatHeader(
            Map<String, CellStyle> noteStyleMap,
            Map<String, String> absenceDescriptionMap,
            Font fontBlack9InSize,
            Font font12InSize,
            Sheet sheet,
            Workbook workbook) {
        Row headerRow = sheet.createRow(0);
        Cell titleCell = ExcelUtil.getCell(headerRow, 1);
        titleCell.setCellValue("BẢNG CHẤM CÔNG THÁNG " + month + " NĂM " + year);
        titleCell.setCellStyle(ExcelUtil.createAlignedCellStyle(
                font12InSize,
                HorizontalAlignment.LEFT,
                VerticalAlignment.CENTER,
                workbook));
        // include notes
        int startIndex = START_OF_DAY_COLUMN;
        for (Map.Entry<String, CellStyle> entry : noteStyleMap.entrySet()) {
            formatNote(headerRow, fontBlack9InSize, entry.getValue(), absenceDescriptionMap.get(entry.getKey()), startIndex, workbook);
            startIndex += 4;
        }
    }

    private void formatBorder(List<Employee> employees, Sheet sheet, Workbook workbook) {
        for (int i = 0; i < employees.size(); i++) {
            Row row = sheet.getRow(START_OF_NAME_ROW + i);
            for (int j = 0; j < TOTAL_TEMPLATE_COLUMNS + numberOfDays; j++) {
                Cell cell = ExcelUtil.getCell(row, j);
                cell.setCellStyle(ExcelUtil.createBorderCellStyle(IndexedColors.BLACK, workbook));
            }
        }
    }

    private Map<String, CellStyle> populateNoteStyleMap(Font font, Workbook workbook) {
        Map<String, ObjectUtil.RGBColor> reportColorMap = settingService.getReportColors();
        Map<String, CellStyle> noteStyleMap = new HashMap<>();
        for (Map.Entry<String, ObjectUtil.RGBColor> entry : reportColorMap.entrySet()) {
            ObjectUtil.RGBColor rgbColor = entry.getValue();
            Color bgColor = ExcelUtil.getBackgroundColor(rgbColor.red(), rgbColor.green(), rgbColor.blue(), workbook);
            noteStyleMap.put(
                    entry.getKey(),
                    ExcelUtil.createBorderCenterBackgroundStyle(
                            font,
                            IndexedColors.BLACK,
                            bgColor,
                            HorizontalAlignment.CENTER,
                            VerticalAlignment.CENTER,
                            workbook));
        }

        return noteStyleMap;
    }


    private Map<String, CellStyle> populateHolidayStyleMap(Font font, Workbook workbook) {
        Map<String, CellStyle> holidayStyleMap = new HashMap<>();
        holidayStyleMap.put(
                HOLIDAY,
                ExcelUtil.createBorderCenterBackgroundStyle(
                        font,
                        IndexedColors.BLACK,
                        ExcelUtil.getHolidayBackgroundColor(workbook),
                        HorizontalAlignment.CENTER,
                        VerticalAlignment.CENTER,
                        workbook));
        return holidayStyleMap;
    }

    private void formatNote(
            Row headerRow,
            Font font,
            CellStyle cellStyle,
            String absenceType,
            int startIndex,
            Workbook workbook) {
        ExcelUtil.getCell(
                headerRow,
                startIndex,
                cellStyle);
        Cell cell = ExcelUtil.getCell(
                headerRow,
                startIndex + 1,
                ExcelUtil.createAlignedCellStyle(font, HorizontalAlignment.LEFT, VerticalAlignment.CENTER, workbook));
        cell.setCellValue(absenceType);
    }

    private void formatDayCell(
            Font font12InSize,
            Sheet sheet,
            Workbook workbook) {
        Row subtitleRow = sheet.getRow(1);
        Row dayRow = sheet.getRow(2);
        for (int i = START_OF_DAY_COLUMN; i < numberOfDays + START_OF_DAY_COLUMN; i++) {
            Integer date = dates.get(i - START_OF_DAY_COLUMN);
            formatSubtitleCell(subtitleRow, i, workbook);
            formatDayCell(dayRow, i, startDate, date, font12InSize, workbook);
            sheet.setColumnWidth(i, 1135);
        }
        formatSubtitleValueCell(month, year, numberOfDays, font12InSize, sheet, workbook);
    }

    private void formatSubtitleCell(Row subtitleRow, int cellIndex, Workbook workbook) {
        Cell subtitleCell = ExcelUtil.getCell(subtitleRow, cellIndex);
        subtitleCell.setCellStyle(ExcelUtil.createBorderCenterBackgroundStyle(
                null,
                IndexedColors.BLACK,
                ExcelUtil.getSubtitleBackgroundColor(workbook),
                HorizontalAlignment.CENTER,
                VerticalAlignment.CENTER,
                workbook));
    }

    private void formatDayCell(Row dayRow,
                               int cellIndex,
                               LocalDate startDate,
                               Integer date,
                               Font font,
                               Workbook workbook) {
        Cell dayCell = ExcelUtil.getCell(dayRow, cellIndex);
        dayCell.setCellValue(startDate.plusDays(cellIndex - START_OF_DAY_COLUMN).getDayOfMonth());

        if (DateUtil.isWeekend(date))
            dayCell.setCellStyle(ExcelUtil.createBorderCenterBackgroundStyle(
                    font,
                    IndexedColors.BLACK,
                    ExcelUtil.getWeekendBackgroundColor(workbook),
                    HorizontalAlignment.CENTER,
                    VerticalAlignment.CENTER,
                    workbook));
        else
            dayCell.setCellStyle(ExcelUtil.createBorderCenterBackgroundStyle(
                    font,
                    IndexedColors.BLACK,
                    ExcelUtil.getWeekDayBackgroundColor(workbook),
                    HorizontalAlignment.CENTER,
                    VerticalAlignment.CENTER,
                    workbook));
    }

    private void formatSubtitleValueCell(String month, String year, int numberOfDays, Font font, Sheet sheet, Workbook workbook) {
        ExcelUtil.setCellValue(1, 3, "Tháng " + month + " năm " + year,
                ExcelUtil.createBorderCenterBackgroundStyle(
                        font,
                        IndexedColors.BLACK,
                        ExcelUtil.getSubtitleBackgroundColor(workbook),
                        HorizontalAlignment.CENTER,
                        VerticalAlignment.CENTER,
                        workbook), sheet);
        ExcelUtil.mergeColumn(1, 1, 3, 3 + numberOfDays - 1, sheet);
    }

    private void populateNormalAttendanceTime(
            Row row,
            CellStyle blackFontBlackBorderCenterCellStyle,
            Map<AttendanceMapKey, AttendanceMapValue> attendanceTimes,
            Map<Long, AbsenceType> absenceTypeMap,
            Map<String, CellStyle> noteStyleMap,
            Map<String, CellStyle> holidayStyleMap,
            int employeeId,
            AttendanceRange attendanceRange
    ) {
        double fullHours = attendanceRange.getFullHours();
        int bonusTime = attendanceRange.getBonusTime();
        for (int j = START_OF_DAY_COLUMN; j < numberOfDays + START_OF_DAY_COLUMN; j++) {
            Double attendanceTime;
            Cell attendanceTimeCell = ExcelUtil.getCell(row, j, blackFontBlackBorderCenterCellStyle);
            Integer date = dates.get(j - START_OF_DAY_COLUMN);
            AttendanceMapKey attendanceMapKey = AttendanceMapKey
                    .builder()
                    .employeeId(employeeId)
                    .date(date)
                    .build();
            AttendanceMapValue value = attendanceTimes.get(attendanceMapKey);
            if (holidays.contains(date)) {
                attendanceTimeCell.setCellStyle(holidayStyleMap.get(HOLIDAY));
                continue;
            }
            if (DateUtil.isWeekend(date) || value == null) continue;
            attendanceTime = value.getAttendanceTime();
            Integer attendanceStatus = value.getAttendanceStatus();
            double bonusTimeInHours = DateUtil.convertMinuteToHour(bonusTime);
            double extraTime = holidayService.getBonusTime(date);
            attendanceTimeCell.setCellValue(Math.min(attendanceTime + bonusTimeInHours + extraTime, FULL_HOURS));
            if (attendanceStatus > 1000) { // check logic
                Long absenceTypeId = attendanceStatus - 1000L;
                AbsenceType absenceType = absenceTypeMap.get(absenceTypeId);
                if (absenceType.getType() == AbsenceTypeEnum.ABSENCE.ordinal() && !absenceType.getCode().equals(NO_ABSENCE)) {
                    double leaveTime = value.getLeaveTime();
                    if (value.getStartTime() == null || leaveTime == fullHours) attendanceTimeCell.setCellValue(0.0);
                    else {
                        attendanceTimeCell.setCellValue(attendanceRangeService.getAttendanceTime(
                                attendanceRange,
                                leaveTime,
                                value.getStartTime().toLocalTime(),
                                value.getEndTime().toLocalTime())
                        );
                    }
                }
                if (noteStyleMap.get(absenceType.getCode()) != null) {
                    attendanceTimeCell.setCellStyle(noteStyleMap.get(absenceType.getCode()));
                }
            }
        }
    }

    @Builder
    @Getter
    static class AttendanceMapKey {
        private int employeeId;
        private int date;

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AttendanceMapKey that = (AttendanceMapKey) o;
            return employeeId == that.employeeId && date == that.date;
        }

        @Override
        public int hashCode() {
            return Objects.hash(employeeId, date);
        }
    }

    @Builder
    @Getter
    static class AttendanceMapValue {
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Double attendanceTime;
        private Double leaveTime;
        private Integer attendanceStatus;
    }
}
