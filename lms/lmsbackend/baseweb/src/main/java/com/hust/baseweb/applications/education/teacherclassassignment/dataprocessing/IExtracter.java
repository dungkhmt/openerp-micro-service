package com.hust.baseweb.applications.education.teacherclassassignment.dataprocessing;

public interface IExtracter {

    void getIndexOfColumnIn(String sheetName);

    void extract();

    String toJson();
}
