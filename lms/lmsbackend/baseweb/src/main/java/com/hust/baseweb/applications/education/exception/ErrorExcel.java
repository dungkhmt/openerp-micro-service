package com.hust.baseweb.applications.education.exception;


import com.hust.baseweb.applications.education.exception.CustomExceptionExcel;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@Getter
public enum ErrorExcel {

    classId_error(0, "Mã lớp lỗi tại :"),
    attachedClassId_error(1, "Mã lớp kèm lỗi tại các hàng:"),
    courseId_error(2, "Mã học phần lỗi tại các hàng: "),
    credit_error(3, "Khối lượng lỗi tại các hàng: "),
    note_error(4, "Ghi chú lỗi tại các hàng: "),
    dayOfWeek_error(5, "Thứ lỗi tại các hàng: "),
    startTime_error(6, "Thời gian bắt đầu lỗi tại các hàng: "),
    endTime_error(7, "Thời gian bắt đầu lỗi tại các hàng: "),
    shift_error(8, "Kíp lỗi tại các hàng: "),
    weeks_error(9, "Tuần lỗi tại các hàng: "),
    room_error(10, "Phòng lỗi tại các hàng: "),
    needExperiment_error(11, "Cần_TN lỗi tại các hàng: "),
    numRegistration_error(12, "SLĐK lỗi tại các hàng: "),
    maxQuantity_error(13, "SL_MAX lỗi tại các hàng: "),
    status_error(14, "Trạng thái lỗi tại các hàng: "),
    classType_error(15, "Loại lớp lỗi tại các hàng: "),
    managementId_error(16, "Mã_QL lỗi tại các hàng: "),
    name_error(17, "Tên học phần lỗi tại các hàng: "),
    eName_error(18, "Tên học phần tiếng anh lỗi tại các hàng: "),
    department_error(19, "Khoa viện lỗi tại các hàng: ");

    private final int code;
    private final String description;

    private static HashMap<ErrorExcel, List<Integer>> errorListHashMap = new HashMap<>();
    //private static HashMap<Integer, List<Integer>> errorListHashMap = new HashMap<>();


    ErrorExcel(int code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public String toString() {
        return code + ": " + description;
    }


    public static String handleError(LinkedHashMap<CustomExceptionExcel, Integer> errorLists) {
        StringBuilder str = new StringBuilder();
        errorListHashMap.put(ErrorExcel.classId_error, new ArrayList<>());
        errorListHashMap.put(ErrorExcel.attachedClassId_error, new ArrayList<>());
        errorListHashMap.put(ErrorExcel.courseId_error, new ArrayList<>());
        errorListHashMap.put(ErrorExcel.credit_error, new ArrayList<>());
        errorListHashMap.put(ErrorExcel.note_error, new ArrayList<>());
        errorListHashMap.put(ErrorExcel.status_error, new ArrayList<>());
        errorListHashMap.put(ErrorExcel.dayOfWeek_error, new ArrayList<>());
        errorListHashMap.put(ErrorExcel.startTime_error, new ArrayList<>());
        errorListHashMap.put(ErrorExcel.endTime_error, new ArrayList<>());
        errorListHashMap.put(ErrorExcel.shift_error, new ArrayList<>());
        errorListHashMap.put(ErrorExcel.weeks_error, new ArrayList<>());
        errorListHashMap.put(ErrorExcel.room_error, new ArrayList<>());
        errorListHashMap.put(ErrorExcel.needExperiment_error, new ArrayList<>());
        errorListHashMap.put(ErrorExcel.numRegistration_error, new ArrayList<>());
        errorListHashMap.put(ErrorExcel.maxQuantity_error, new ArrayList<>());
        errorListHashMap.put(ErrorExcel.classType_error, new ArrayList<>());
        errorListHashMap.put(ErrorExcel.managementId_error, new ArrayList<>());
        errorListHashMap.put(ErrorExcel.name_error, new ArrayList<>());
        errorListHashMap.put(ErrorExcel.eName_error, new ArrayList<>());
        errorListHashMap.put(ErrorExcel.department_error, new ArrayList<>());
        for (CustomExceptionExcel c : errorLists.keySet()) {
            int k = errorLists.get(c);
            for (ErrorExcel error : c.getListError()) {
                int code = error.getCode();
                switch (code) {
                    case 0:
                        errorListHashMap.get(ErrorExcel.classId_error).add(k);
                        break;
                    case 1:
                        errorListHashMap.get(ErrorExcel.attachedClassId_error).add(k);
                        break;
                    case 2:
                        errorListHashMap.get(ErrorExcel.courseId_error).add(k);
                        break;
                    case 3:
                        errorListHashMap.get(ErrorExcel.credit_error).add(k);
                        break;
                    case 4:
                        errorListHashMap.get(ErrorExcel.note_error).add(k);
                        break;
                    case 5:
                        errorListHashMap.get(ErrorExcel.dayOfWeek_error).add(k);
                        break;
                    case 6:
                        errorListHashMap.get(ErrorExcel.startTime_error).add(k);
                        break;
                    case 7:
                        errorListHashMap.get(ErrorExcel.endTime_error).add(k);
                        break;
                    case 8:
                        errorListHashMap.get(ErrorExcel.shift_error).add(k);
                        break;
                    case 9:
                        errorListHashMap.get(ErrorExcel.weeks_error).add(k);
                        break;
                    case 10:
                        errorListHashMap.get(ErrorExcel.room_error).add(k);
                        break;
                    case 11:
                        errorListHashMap.get(ErrorExcel.needExperiment_error).add(k);
                        break;
                    case 12:
                        errorListHashMap.get(ErrorExcel.numRegistration_error).add(k);
                        break;
                    case 13:
                        errorListHashMap.get(ErrorExcel.maxQuantity_error).add(k);
                        break;
                    case 14:
                        errorListHashMap.get(ErrorExcel.status_error).add(k);
                        break;
                    case 15:
                        errorListHashMap.get(ErrorExcel.classType_error).add(k);
                        break;
                    case 16:
                        errorListHashMap.get(ErrorExcel.managementId_error).add(k);
                        break;
                    case 17:
                        errorListHashMap.get(ErrorExcel.name_error).add(k);
                        break;
                    case 18:
                        errorListHashMap.get(ErrorExcel.eName_error).add(k);
                        break;
                    case 19:
                        errorListHashMap.get(ErrorExcel.department_error).add(k);
                        break;
                }

            }
        }


        for (ErrorExcel error : errorListHashMap.keySet()) {
            if (errorListHashMap.get(error).size() > 0) {
                str.append(error.getDescription()).append("\n");
                for (int i : errorListHashMap.get(error)) {
                    str.append(i).append("\n");
                }
            }
        }

        return str.toString();
    }

}

