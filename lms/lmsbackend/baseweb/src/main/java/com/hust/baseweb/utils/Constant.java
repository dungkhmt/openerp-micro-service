package com.hust.baseweb.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public class Constant {

    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter LOCAL_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter LOCAL_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static final DateFormat ORDER_EXCEL_DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");
    static final String GOOGLE_MAP_API_KEY_FILE = "gg_api_key.txt";

}
