package com.hust.baseweb.applications.education.teacherclassassignment.utils;

import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Refactoring done
 */
@Log4j2
public class TimetableConflictChecker {

    static class SlotMapping {

        public HashMap<String, String> mStartSlotStr2SlotCode = new HashMap<>();

        public HashMap<String, String> mEndSlotStr2SlotCode = new HashMap<>();

        public SlotMapping() {
            // Map starting slot
            mStartSlotStr2SlotCode.put("10645", "11");
            mStartSlotStr2SlotCode.put("10730", "12");
            mStartSlotStr2SlotCode.put("10825", "13");
            mStartSlotStr2SlotCode.put("10920", "14");
            mStartSlotStr2SlotCode.put("11015", "15");
            mStartSlotStr2SlotCode.put("11100", "16");
            mStartSlotStr2SlotCode.put("21230", "21");
            mStartSlotStr2SlotCode.put("21315", "22");
            mStartSlotStr2SlotCode.put("21410", "23");
            mStartSlotStr2SlotCode.put("21505", "24");
            mStartSlotStr2SlotCode.put("21600", "25");
            mStartSlotStr2SlotCode.put("21645", "26");

            // Map ending slot
            mEndSlotStr2SlotCode.put("10730", "11");
            mEndSlotStr2SlotCode.put("10815", "12");
            mEndSlotStr2SlotCode.put("10910", "13");
            mEndSlotStr2SlotCode.put("11005", "14");
            mEndSlotStr2SlotCode.put("11100", "15");
            mEndSlotStr2SlotCode.put("11145", "16");
            mEndSlotStr2SlotCode.put("21315", "21");
            mEndSlotStr2SlotCode.put("21400", "22");
            mEndSlotStr2SlotCode.put("21455", "23");
            mEndSlotStr2SlotCode.put("21550", "24");
            mEndSlotStr2SlotCode.put("21645", "25");
            mEndSlotStr2SlotCode.put("21730", "26");
            mEndSlotStr2SlotCode.put("21735", "26");
        }

        public String getStartSlotCode(String s) {
            return mStartSlotStr2SlotCode.get(s);
        }

        public String getEndSlotCode(String s) {
            return mEndSlotStr2SlotCode.get(s);
        }
    }

    public static SlotMapping slotMapping = new SlotMapping();

    public static String name() {
        return "TimetableConflictChecker";
    }

    public static String getDayOfWeek(String s) {
        return s.substring(0, 1);
    }

    // s under format, e.g. 21505 (15h05 chieu)
    public static String getSlotStr(String s) {
        return s.substring(1);
    }

    /**
     * Convert format 6 digits to 3 digits
     *
     * @param s in format 6 digits
     * @return 3 digits format
     */
    public static String convertStartSlotStr2Code(String s) {
        String d = getDayOfWeek(s);
        String slots = getSlotStr(s);

        return d + slotMapping.getStartSlotCode(slots);
    }

    /**
     * Convert format 6 digits to 3 digits
     *
     * @param s in format 6 digits
     * @return 3 digits format
     */
    public static String convertEndSlotStr2Code(String s) {
        String d = getDayOfWeek(s);
        String slots = getSlotStr(s);

        return d + slotMapping.getEndSlotCode(slots);
    }

    /**
     * @param timeTableCode in format "1,221,223" or "1,221505,221730"
     * @return 3 digits format
     */
    public static String extractPeriod(String timeTableCode) {
        timeTableCode = timeTableCode.replaceAll("\\s", "");
        if (timeTableCode.length() < 9) {
            return null;
        }

        // Format 3 digits
        String str = timeTableCode.substring(2, 9);
        String[] s = str.split(",");

        if (2 == s.length && 3 == s[0].length() && 3 == s[1].length()) {
            return str; // "221,223"
        }

        // Format 6 digits
        str = timeTableCode.substring(2, 15);
        s = str.split(",");
        return convertStartSlotStr2Code(s[0]) + "," + convertEndSlotStr2Code(s[1]);
    }

    /**
     * @param timeTable in format "1,221,223" or "1,221505,221730"
     * @return {@link TimeTableStartAndDuration}
     */
    public static TimeTableStartAndDuration extractFromString(String timeTable) {
        try {
            String code = extractPeriod(timeTable);
            if (null == code || code.equals("")) { // TODO: check if case exist: code.equals("")
                return null;
            }

            String[] p = code.split(","); // ["221","223"]
            if (p.length <
                2) { // TODO: check if case exist: check case 6 digits of extractPeriod(), may be replace < with !=
                return null;
            }

//            log.info("extractFromString(" + timeTable + "), extract code = " + code
//                     + " p[0] = " + p[0] + ", p[1] = " + p[1]);

            int startSlot;
            int endSlot;
            int duration;
            int dayOfWeek = Integer.parseInt(p[0].substring(0, 1));
            int session = Integer.parseInt(p[0].substring(1, 2));
            int startPeriod = Integer.parseInt(p[0].substring(2, 3));
            int endPeriod = Integer.parseInt(p[1].substring(2, 3));

            if (1 == session) { // MORNING
                startSlot = (dayOfWeek - 2) * 12 + startPeriod;
                endSlot = (dayOfWeek - 2) * 12 + endPeriod;
            } else { // session == 2: AFTERNOON
                startSlot = (dayOfWeek - 2) * 12 + startPeriod + 6; // 221 --> 7
                endSlot = (dayOfWeek - 2) * 12 + endPeriod + 6; // 223 --> 9
            }

            duration = endSlot - startSlot + 1;

            return new TimeTableStartAndDuration(dayOfWeek, startSlot, endSlot, duration);
        } catch (Exception e) {
            System.out.println(timeTable);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Extract days of week of learning sessions
     *
     * @param timetable in format "1,325,326,2-9,11-18,B1-402;2,221,222,2-9,11-18,B1-402;"
     * @return
     */
    public static HashSet<Integer> extractDayOfTimeTable(String timetable) {
        timetable = timetable.replaceAll("\\s", "");
        String[] learningSessions = timetable.split(";"); // ["1,325,326,2-9,11-18,B1-402", "2,221,222,2-9,11-18,B1-402"]
        HashSet<Integer> daysOfWeek = new HashSet<>();

        for (String session : learningSessions) {
            TimeTableStartAndDuration ttsd = extractFromString(session);

            if (null != ttsd) {
                daysOfWeek.add(ttsd.getDay());
            } else {
//                log.info("extractDayOfTimeTable, invalid timetable " + timetable);
                return null;
            }
        }

        return daysOfWeek;
    }

    // Todo: sua lai de khi it nhat mot trong hai k hop le thi tra ve false

    /**
     * Check if two timetables are conflict
     *
     * @param timetableCode1
     * @param timetableCode2
     * @return {@code true} when exist two conflicting valid sessions
     */
    public static boolean conflictMultiTimeTable(String timetableCode1, String timetableCode2) {
        String[] s1 = timetableCode1.split(";");
        String[] s2 = timetableCode2.split(";");

        if (0 == s1.length || 0 == s2.length) {
            return false;
        }

        for (String v1 : s1) {
            String t1 = v1.trim();

            if (!"".equals(t1)) {
                for (String v2 : s2) {
                    String t2 = v2.trim();

                    if (!"".equals(t2)) {
                        if (conflict(t1, t2)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * @param timetableCode1
     * @param timetableCode2
     * @return {@code true} when two sessions are (valid and conflict)
     */
    public static boolean conflict(String timetableCode1, String timetableCode2) {
        try {
            String code1 = extractPeriod(timetableCode1);
            String code2 = extractPeriod(timetableCode2);

            if (null == code1 || null == code2) {
//                log.info("TimetableConflictChecker::conflict, invalid timeTableCode"
//                         + ", timeTableCode1 = " + timetableCode1
//                         + ", timeTableCode2 = " + timetableCode2);

                return false;
            }

//            log.info("TimetableConflictChecker::conflict, code1 = " + code1 + ", code2 = " + code2);

            String[] p1 = code1.split(",");
            String[] p2 = code2.split(",");

            int start1 = Integer.parseInt(p1[0]);
            int end1 = Integer.parseInt(p1[1]);
            int start2 = Integer.parseInt(p2[0]);
            int end2 = Integer.parseInt(p2[1]);

//            log.info("start1 = " + start1 + " end1 = " + end1 + " start2 = " + start2 + " end2 = " + end2);

            return !(start1 > end2 || start2 > end1);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("TimetableConflictChecker.conflict, EXCEPTION "
                     + "t1 = " + timetableCode1
                     + ", t2 = " + timetableCode2);

            return false;
        }
    }

    public static void main(String[] args) {
        String t1 = "1,312,314,2-9,11-18,D9-107;";
        String t2 = "1,411,414,2-9,11-18,D9-101;";
        System.out.println(TimetableConflictChecker.conflict(t1, t2));

        String t = "1,221505,221730";
        String c = TimetableConflictChecker.extractPeriod(t);
        System.out.println(c);

        System.out.println(TimetableConflictChecker.extractFromString(t2).toString());
    }
}
