package com.hust.baseweb.applications.education.teacherclassassignment.dataprocessing;

import com.google.gson.Gson;
import com.hust.baseweb.applications.education.teacherclassassignment.model.AlgoClassIM;
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
public class ClassExtracter implements IExtracter {

    private Map<String, Integer> indexOfColumn;

    private FileInputStream file;

    private XSSFWorkbook workbook;

    private XSSFSheet sheet;

    private Iterator<Row> rowIterator;

    private ArrayList<AlgoClassIM> classes = new ArrayList<>();

    private HashMap<AlgoClassIM, String> mClass2PreAssignedTeacher;

    public ClassExtracter(FileInputStream file) throws IOException {
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

            switch (cell.getStringCellValue().toLowerCase()) {
                case "mã lớp":
                    if (!indexOfColumn.containsKey("id")) {
                        indexOfColumn.put("id", i);
                    }
                    break;
                case "classtype":
                    if (!indexOfColumn.containsKey("classType")) {
                        indexOfColumn.put("classType", i);
                    }
                    break;
                case "mã hp":
                    if (!indexOfColumn.containsKey("course_id")) {
                        indexOfColumn.put("course_id", i);
                    }
                    break;
                case "session":
                    if (!indexOfColumn.containsKey("timetable")) {
                        indexOfColumn.put("timetable", i);
                    }
                    break;
                case "tên hp":
                    if (!indexOfColumn.containsKey("name")) {
                        indexOfColumn.put("name", i);
                    }
                    break;
                case "emails":
                    if (!indexOfColumn.containsKey("email")) {
                        indexOfColumn.put("email", i);
                    }
            }
        }
    }

    public HashMap getMapClass2PreassignedTeacher() {
        return mClass2PreAssignedTeacher;
    }

    public String getTeacherAssigned2Class(AlgoClassIM cls) {
        return mClass2PreAssignedTeacher.get(cls);
    }

    @Override
    public void extract() {
        Row row;
        Map<Integer, Integer[]> timetable = new HashMap<>();

        timetable.put(1, new Integer[]{405, 450});
        timetable.put(2, new Integer[]{450, 495});
        timetable.put(3, new Integer[]{505, 550});
        timetable.put(4, new Integer[]{560, 605});
        timetable.put(5, new Integer[]{615, 660});
        timetable.put(6, new Integer[]{660, 705});
        timetable.put(7, new Integer[]{750, 795});
        timetable.put(8, new Integer[]{795, 840});
        timetable.put(9, new Integer[]{850, 895});
        timetable.put(10, new Integer[]{905, 950});
        timetable.put(11, new Integer[]{960, 1005});
        timetable.put(12, new Integer[]{1005, 1050});
        timetable.put(13, new Integer[]{1065, 1110});
        timetable.put(14, new Integer[]{1110, 1155});

        mClass2PreAssignedTeacher = new HashMap<AlgoClassIM, String>();
        while (rowIterator.hasNext()) {
            row = rowIterator.next();
            AlgoClassIM classIM = new AlgoClassIM();

            classes.add(classIM);

            classIM.setId((int) row.getCell(indexOfColumn.get("id")).getNumericCellValue());
            classIM.setClassType(row.getCell(indexOfColumn.get("classType")).getStringCellValue());
            classIM.setCourseId(row.getCell(indexOfColumn.get("course_id")).getStringCellValue());
            classIM.setCourseName(row.getCell(indexOfColumn.get("name")).getStringCellValue());
            classIM.setTimetable(row.getCell(indexOfColumn.get("timetable")).getStringCellValue());

            String teacherId = row.getCell(indexOfColumn.get("email")).getStringCellValue();
            if (teacherId != null && !teacherId.equals("")) {
                mClass2PreAssignedTeacher.put(classIM, teacherId);
            }

            // Calculate hourLoad.
            String[] sessions = classIM.getTimetable().split(";");
            double total = 0;

            for (String session : sessions) {
                String[] info = session.split(",");

                if ("TN".equalsIgnoreCase(classIM.getClassType())) {
                    int start;
                    int end;

                    if (info[1].length() == 3) {
                        if (info[2].length() != 3) {
                            System.out.println(classIM.getId());
                        }
                        if (!info[1].substring(0, 1).equals(info[2].substring(0, 1))) {
                            System.out.println(classIM.getId());
                        }
                        if (!info[1].substring(1, 2).equals(info[2].substring(1, 2))) {
                            System.out.println(classIM.getId());
                        }

                        start = timetable.get((Integer.parseInt(info[1].substring(1, 2)) - 1) * 6 +
                                              Integer.parseInt(info[1].substring(2, 3)))[0];
                        end = timetable.get((Integer.parseInt(info[2].substring(1, 2)) - 1) * 6 +
                                            Integer.parseInt(info[2].substring(2, 3)))[1];
                    } else {
                        if (info[1].length() != 6) {
                            System.out.println(classIM.getId());
                        }
                        if (info[2].length() != 6) {
                            System.out.println(classIM.getId());
                        }
                        if (!info[1].substring(0, 1).equals(info[2].substring(0, 1))) {
                            System.out.println(classIM.getId());
                        }
                        if (!info[1].substring(1, 2).equals(info[2].substring(1, 2))) {
                            System.out.println(classIM.getId());
                        }

                        start = Integer.parseInt(info[1].substring(2, 4)) * 60 +
                                Integer.parseInt(info[1].substring(4, 6));
                        end = Integer.parseInt(info[2].substring(2, 4)) * 60 +
                              Integer.parseInt(info[2].substring(4, 6));
                    }

                    total += (end - start);
                } else {
                    if (info[1].length() != 3) {
                        System.out.println(classIM.getId());
                    }
                    if (info[2].length() != 3) {
                        System.out.println(classIM.getId());
                    }
                    if (!info[1].substring(0, 1).equals(info[2].substring(0, 1))) {
                        System.out.println(classIM.getId());
                    }
                    if (!info[1].substring(1, 2).equals(info[2].substring(1, 2))) {
                        System.out.println(classIM.getId());
                    }

                    total += (Integer.parseInt(info[2].substring(2, 3)) - Integer.parseInt(info[1].substring(2, 3)) +
                              1) * 45;
                }
            }

            classIM.setHourLoad(total * 1.0 / 60);
        }
    }

    @Override
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(classes);
    }

    public void close() throws IOException {
        file.close();
    }
}
