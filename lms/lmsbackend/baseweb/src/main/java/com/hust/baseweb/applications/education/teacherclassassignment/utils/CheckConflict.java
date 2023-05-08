//package com.hust.baseweb.applications.education.teacherclassassignment.utils;
//
//import com.hust.baseweb.applications.education.teacherclassassignment.model.AlgoClassIM;
//import com.hust.baseweb.applications.education.teacherclassassignment.model.ExtractTimetable;
//import org.apache.commons.lang3.StringUtils;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
///**
// * Consider removing, use {@link TimetableConflictChecker} instead
// */
//public class CheckConflict {
//
//    private Map<Integer, String[]> period;
//
//    private Map<Integer, ExtractTimetable> extractedTimetable;
//
//    public CheckConflict() {
//        extractedTimetable = new HashMap<>();
//        period = new HashMap<>();
//
//        period.put(1, new String[]{"0645", "0730"});
//        period.put(2, new String[]{"0730", "0815"});
//        period.put(3, new String[]{"0825", "0910"});
//        period.put(4, new String[]{"0920", "1005"});
//        period.put(5, new String[]{"1015", "1100"});
//        period.put(6, new String[]{"1100", "1145"});
//
//        period.put(7, new String[]{"1230", "1315"});
//        period.put(8, new String[]{"1315", "1400"});
//        period.put(9, new String[]{"1410", "1455"});
//        period.put(10, new String[]{"1505", "1550"});
//        period.put(11, new String[]{"1600", "1645"});
//        period.put(12, new String[]{"1645", "1730"});
//
//        period.put(13, new String[]{"1745", "1830"});
//        period.put(14, new String[]{"1830", "1915"});
//    }
//
//    private ExtractTimetable extractTimetable(AlgoClassIM classIM) {
//        if (extractedTimetable.containsKey(classIM.getId())) {
//            return extractedTimetable.get(classIM.getId());
//        } else {
//            ExtractTimetable timetable = new ExtractTimetable();
//            List<String[]> sessions = Arrays
//                .stream(classIM.getTimetable().split(";"))
//                .map(session -> session.split(","))
//                .collect(
//                    Collectors.toList());
//
//            int noSessions = sessions.size();
//            String[] start = new String[noSessions];
//            String[] end = new String[noSessions];
//            Set<Integer>[] weeks = new Set[noSessions];
//
//            for (int i = 0; i < noSessions; i++) {
//                start[i] = normalize(StringUtils.deleteWhitespace(sessions.get(i)[1]), true);
//                end[i] = normalize(StringUtils.deleteWhitespace(sessions.get(i)[2]), false);
//                weeks[i] = new HashSet();
//
//                List<String[]> weekStr = Arrays
//                    .stream(Arrays.copyOfRange(sessions.get(i), 3, sessions.get(i).length - 1))
//                    .map(ele -> StringUtils.deleteWhitespace(ele).split("-")).collect(Collectors.toList());
//
//                for (String[] ele : weekStr) {
//                    if (ele.length == 1) {
//                        weeks[i].add(Integer.parseInt(ele[0]));
//                    } else {
//                        int from = Integer.parseInt(ele[0]);
//                        int to = Integer.parseInt(ele[1]) + 1;
//
//                        for (int j = from; j < to; j++) {
//                            weeks[i].add(j);
//                        }
//                    }
//                }
//            }
//
//            timetable.setStart(start);
//            timetable.setEnd(end);
//            timetable.setWeeks(weeks);
//
//            extractedTimetable.put(classIM.getId(), timetable);
//            return timetable;
//        }
//    }
//
//    private String normalize(String timetable, boolean start) {
//        String result;
//
//        switch (timetable.length()) {
//            case 3:
//                final int period = (Integer.parseInt(timetable.substring(1, 2)) - 1) * 6 +
//                                   Integer.parseInt(timetable.substring(2, 3));
//
//                if (start) {
//                    result = timetable.substring(0, 2) +
//                             this.period.get(period)[0];
//                } else {
//                    result = timetable.substring(0, 2) +
//                             this.period.get(period)[1];
//                }
//                break;
//            default:
//                result = timetable;
//        }
//
//        return result;
//    }
//
//    public boolean isConflict(AlgoClassIM cls1, AlgoClassIM cls2) {
//        ExtractTimetable timetable1 = extractTimetable(cls1);
//        ExtractTimetable timetable2 = extractTimetable(cls2);
//
//        for (int i = 0; i < timetable1.getStart().length; i++) {
//            for (int j = 0; j < timetable2.getStart().length; j++) {
//                // Session does not overlap.
//                if (timetable1.getEnd()[i].compareTo(timetable2.getStart()[j]) < 0 ||
//                    timetable2.getEnd()[j].compareTo(timetable1.getStart()[i]) < 0) {
//                } else { // Sessions overlap.
//                    Set<Integer> intersection = new HashSet<>(timetable1.getWeeks()[i]);
//                    intersection.retainAll(timetable2.getWeeks()[j]);
//
//                    if (intersection.size() > 0) {
//                        return true;
//                    }
//                }
//            }
//        }
//
//        return false;
//    }
//
//    public static void main(String[] args) {
//        CheckConflict checker = new CheckConflict();
//        AlgoClassIM cls1 = new AlgoClassIM();
//        AlgoClassIM cls2 = new AlgoClassIM();
//
//        cls1.setId(121294);
//        cls1.setTimetable(
//            "1,215,216,2-9,11-18,D9-403;2,315,316,2-9,11-18,D9-403;3,515,516,2-9,11-18,D9-403;4,615,616,2-9,11-18,D9-403;");
//
//        cls2.setId(121295);
//        cls2.setTimetable("1,221,224,2-9,11-18,D5-304;");
//
//        System.out.println(checker.isConflict(cls1, cls2));
//        /*System.out.println(checker.extractedTimetable.get(cls1.getId()).getStart()[0]);
//        System.out.println(checker.extractedTimetable.get(cls2.getId()).getStart()[0]);*/
//    }
//}
