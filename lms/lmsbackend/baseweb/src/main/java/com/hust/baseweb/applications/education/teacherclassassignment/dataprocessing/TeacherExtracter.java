package com.hust.baseweb.applications.education.teacherclassassignment.dataprocessing;

import com.google.gson.Gson;
import com.hust.baseweb.applications.education.teacherclassassignment.model.AlgoTeacherIM;
import com.hust.baseweb.applications.education.teacherclassassignment.model.Course4Teacher;
import lombok.Getter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Getter
public class TeacherExtracter implements IExtracter {

    private Map<String, Integer> indexOfColumn;

    private FileInputStream file;

    private XSSFWorkbook workbook;

    private XSSFSheet sheet;

    private Iterator<Row> rowIterator;

    private ArrayList<AlgoTeacherIM> teachers = new ArrayList<>();

    public TeacherExtracter(FileInputStream file) throws IOException {
        indexOfColumn = new HashMap<>();
        this.file = file;
        workbook = new XSSFWorkbook(file);

    }

    @Override
    public void getIndexOfColumnIn(String sheetName) {
        sheet = workbook.getSheet(sheetName);
        rowIterator = sheet.iterator();
        Row row = rowIterator.next();

        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            System.out.println(cell.getStringCellValue().toLowerCase());
            switch (cell.getStringCellValue().toLowerCase()) {
                case "email":
                    if (!indexOfColumn.containsKey("id")) {
                        indexOfColumn.put("id", i);
                    }
                    break;
                case "teacher":
                    if (!indexOfColumn.containsKey("name")) {
                        indexOfColumn.put("name", i);
                    }
                    break;
                case "course_id":
                    if (!indexOfColumn.containsKey("course_id")) {
                        indexOfColumn.put("course_id", i);
                    }
                    break;
                case "class_type":
                    if (!indexOfColumn.containsKey("type")) {
                        indexOfColumn.put("type", i);
                    }
                    break;
                case "course_name":
                    if (!indexOfColumn.containsKey("course_name")) {
                        indexOfColumn.put("course_name", i);
                    }
                    break;
            }
        }
    }

    @Override
    public void extract() {
        Row row;
        String preTeacher = "";
        HashMap<String, AlgoTeacherIM> mID2Teacher = new HashMap();
        while (rowIterator.hasNext()) {
            row = rowIterator.next();
            String id = row.getCell(indexOfColumn.get("id")).getStringCellValue();

            AlgoTeacherIM teacher = mID2Teacher.get(id);

            /*
            if (preTeacher.equalsIgnoreCase(id)) {
                teacher = teachers.get(teachers.size() - 1);
            } else {

             */
            if (teacher == null) {
                teacher = new AlgoTeacherIM();

                teacher.setId(id);
                teacher.setName(row.getCell(indexOfColumn.get("name")).getStringCellValue());
                teacher.setCourses(new ArrayList<>());

                preTeacher = id;
                teachers.add(teacher);
                mID2Teacher.put(id, teacher);
            }

            teacher.getCourses().add(
                new Course4Teacher(
                    row.getCell(indexOfColumn.get("course_id")).getStringCellValue(),
                    row.getCell(indexOfColumn.get("course_name")).getStringCellValue(),
                    (int)row.getCell(indexOfColumn.get("priority")).getNumericCellValue(),
                    row.getCell(indexOfColumn.get("type")).getStringCellValue()
                )
            );
        }
    }

    @Override
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(teachers);
    }

    public void close() throws IOException {
        file.close();
    }
}
