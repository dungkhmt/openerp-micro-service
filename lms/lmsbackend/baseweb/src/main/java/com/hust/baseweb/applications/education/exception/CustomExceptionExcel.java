package com.hust.baseweb.applications.education.exception;

//import com.hust.baseweb.applications.education.suggesttimetable.enums.ErrorExcel;
import  com.hust.baseweb.applications.education.exception.ErrorExcel;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class CustomExceptionExcel extends Exception {

    List<ErrorExcel> listError = new ArrayList<>();

    public CustomExceptionExcel(List<ErrorExcel> listError) {
        this.listError = listError;
    }

    @Override
    public String getMessage() {
        String str = "";
        for (ErrorExcel exception : listError) {
            str += exception.getDescription() + "\n";
        }
        str = StringUtils.chop(str);
        return str;
    }


    public List<ErrorExcel> getListError() {
        return listError;
    }
}
