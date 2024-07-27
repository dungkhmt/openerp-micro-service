package openerp.openerpresourceserver.generaltimetabling.algorithms;

import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.generaltimetabling.exception.InvalidClassStudentQuantityException;
import openerp.openerpresourceserver.generaltimetabling.exception.InvalidFieldException;
import openerp.openerpresourceserver.generaltimetabling.exception.NotFoundException;
import openerp.openerpresourceserver.generaltimetabling.helper.MassExtractor;
import openerp.openerpresourceserver.generaltimetabling.model.entity.Classroom;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.GeneralClass;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.RoomReservation;
import openerp.openerpresourceserver.generaltimetabling.model.entity.occupation.RoomOccupation;

import java.util.*;

@Log4j2
public class V2ClassScheduler {
    private static int MIN_EVEN_PERIOD = 2;
    private static int MIN_ODD_PERIOD = 3;

    //private RoomOccupationService roomOccupationService;

    public static boolean intersect(List<Integer> L1, List<Integer> L2) {
        for (int i : L1) {
            for (int j : L2) {
                if (i == j) return true;
            }
        }
        return false;
    }

    public static List<GeneralClass> autoScheduleTimeSlot(List<GeneralClass> classes, int timeLimit) {
        int n = classes.size();
        if (n == 0) {
            log.info("Chưa chọn lớp!");
            return null;
        }
        classes.sort(Comparator.comparing(GeneralClass::hasNonNullTimeSlot).reversed());
        List<int[]> conflict = new ArrayList<int[]>();
        int[] durations = classes.stream().filter(c -> c.getMass() != null).mapToInt(c -> MassExtractor.extract(c.getMass())).toArray();
        int[] splitDurations = new int[150];
        int[] days = {0, 2, 4, 1, 3};
        int periodIdx = 0;
        HashMap<Integer, Integer> scheduleMap = new HashMap<>();

        /*Init split durations map*/
        for (int i = 0; i < classes.size(); i++) {
            GeneralClass c = classes.get(i);
            if (c.getTimeSlots().size() > 1) {
                for (RoomReservation rr: c.getTimeSlots()) {
                    if (!rr.isTimeSlotNotNull()) {
                        throw new InvalidFieldException("Lớp " + c.getClassCode() + " có nhiều 2 hơn ca học và có lịch trống!");
                    }
                }
            }

            if (c.getTimeSlots() != null && !c.getTimeSlots().isEmpty()) {
                for (int j = 0; j < c.getTimeSlots().size(); j++) {
                    // If the timeslot is not null then split
                    if (c.getTimeSlots().get(j).isTimeSlotNotNull()) {
                        splitDurations[periodIdx] = c.getTimeSlots().get(j).getEndTime()-c.getTimeSlots().get(j).getStartTime()+1;
                        scheduleMap.put(periodIdx, i);
                        periodIdx += 1;
                    } else {
                        int classDuration = durations[i];
                        if (classDuration % 2 == 0) {
                            //If this class have durations length is even, split to [n/2] sessions 2 periods
                            while (classDuration > 0) {
                                classDuration = classDuration - MIN_EVEN_PERIOD;
                                splitDurations[periodIdx] = MIN_EVEN_PERIOD;
                                scheduleMap.put(periodIdx, i);
                                periodIdx += 1;

                            }
                        } else {
                            //If this class have durations length is odd, split to [n/2 - 1] sessions 2 periods + 1 sessions 3 periods
                            while (classDuration > 1) {
                                if (classDuration == MIN_ODD_PERIOD) {
                                    splitDurations[periodIdx] = MIN_ODD_PERIOD;
                                    scheduleMap.put(periodIdx, i);
                                } else {
                                    splitDurations[periodIdx] = MIN_EVEN_PERIOD;
                                    scheduleMap.put(periodIdx, i);
                                }
                                classDuration = classDuration - MIN_EVEN_PERIOD;
                                periodIdx += 1;

                            }
                        }
                    }
                }
            }
        }



        int totalSessions = periodIdx;


        ArrayList[] domains = new ArrayList[totalSessions];
        int classPeriodIdx = 0;

        for (int i = 0; i < totalSessions; i++) {
            domains[i] = new ArrayList<>();
        }

        for (int i = 0; i < n; i++) {
            GeneralClass c = classes.get(i);
            boolean fixedTimeSlot = false;

            if (c.getTimeSlots() != null && c.getTimeSlots().size() > 0) {
                for (int j = 0; j < c.getTimeSlots().size(); j++) {
                    RoomReservation rr = c.getTimeSlots().get(j);
                    int start = -1;
                    int end = -1;
                    int day = -1;
                    int KIP = -1;
                    if (rr.getStartTime() != null && rr.getStartTime() > 0) {
                        start = rr.getStartTime();
                    }
                    if (rr.getEndTime() != null && rr.getEndTime() > 0) {
                        end = rr.getEndTime();
                    }
                    if (rr.getWeekday() != null && rr.getWeekday() > 0) {
                        day = rr.getWeekday() - 2;// 0 means Monday, 1 means Tuesday...
                    }
                    if (rr.getCrew() != null) {
                        if (rr.getCrew().equals("S")) KIP = 0;
                        else KIP = 1;
                    }
                    if (start > 0 && end > 0 && day > 0) {
                        int s = 12 * day + 6 * KIP + start;
                        domains[classPeriodIdx].add(s);// time-slot is assigned in advance
                        classPeriodIdx += 1;
                    } else {
                        int fKIP = classes.get(scheduleMap.get(classPeriodIdx)).getCrew() == "S" ? 0 : 1;
                        while (classPeriodIdx < totalSessions && scheduleMap.get(classPeriodIdx) == i){
                            for (int fday : days) {
                                for (int fstart = 1; fstart <= 6 - splitDurations[classPeriodIdx] + 1; fstart++) {
                                    int s = 12 * fday + 6 * fKIP + fstart;
                                    domains[classPeriodIdx].add(s);
                                }
                            }
                            classPeriodIdx += 1;
                        }
                    }
                }

            }

        }

        for (int i = 0; i < totalSessions; i++) {
            for (int j = i + 1; j < totalSessions; j++) {
                conflict.add(new int[]{i, j});
            }
        }

        ClassTimeScheduleBacktrackingSolver solver = new ClassTimeScheduleBacktrackingSolver(totalSessions, splitDurations, domains, conflict, timeLimit);
        solver.solve();
        if (!solver.hasSolution()) {
            log.error("NO SOLUTION");
            throw new NotFoundException("Không tìm thấy cách xếp các lớp học!");
        } else {
            int[] solution = solver.getSolution();
            log.info("FOUND SOLUTION");
            for (int i = 0; i < n; i++) {
                GeneralClass gClass = classes.get(i);
                gClass.getTimeSlots().forEach(rr -> rr.setGeneralClass(null));
                gClass.getTimeSlots().clear();
            }

            for (int i = 0; i < totalSessions; i++) {
                int day = solution[i] / 12;
                int t1 = solution[i] - day * 12;
                int K = t1 / 6; // kip
                int tietBD = t1 - 6 * K;
                GeneralClass gClass = classes.get(scheduleMap.get(i));

                RoomReservation newRoomReservation = new RoomReservation(gClass.getCrew(), tietBD, tietBD + splitDurations[i] - 1, day + 2, null);

                newRoomReservation.setGeneralClass(gClass);
                gClass.getTimeSlots().add(newRoomReservation);
                log.info("class[" + i + "] is assigned to slot " + solution[i] + "(" + day + "," + K + "," + tietBD + ")");
            }
        }
        return classes;
    }


    public static List<GeneralClass> autoScheduleRoom(List<GeneralClass> classes, List<Classroom> rooms, int timeLimit, List<RoomOccupation> roomOccupations) {
        /*Validate the timeslot assigned*/
        for (GeneralClass gClass : classes) {
            if (gClass.getTimeSlots().isEmpty()) {
                log.error("Lớp " + gClass + " chưa được gán lịch học!");
                return null;
            }
        }

        HashMap<Integer, Integer> scheduleMap = new HashMap<>();
        int scheduleIdx = 0;
        for (int i = 0; i < classes.size(); i++) {
            for (int j = 0; j < classes.get(i).getTimeSlots().size(); j++) {
                scheduleMap.put(scheduleIdx, i);
                scheduleIdx++;
            }
        }

        int numPeriods = scheduleIdx;
        int numClasses = classes.size();
        /*Initial data*/
        int[] roomCapacities = rooms.stream().mapToInt(room -> Math.toIntExact(room.getQuantityMax())).toArray();
        Arrays.sort(roomCapacities);
        Collections.sort(rooms, Comparator.comparingLong(Classroom::getQuantityMax));

        List<RoomReservation> roomReservations = classes.stream()
                .flatMap(generalClass -> generalClass.getTimeSlots().stream())
                .toList();

        int[] studentQuantities = roomReservations.stream().mapToInt(rr -> {
            if (rr.getGeneralClass().getQuantityMax() == null)
                throw new InvalidClassStudentQuantityException(rr.getGeneralClass().getClassCode() + " đang không có số học sinh tối đa!");
            return rr.getGeneralClass().getQuantityMax();
        }).toArray();

        int numRooms = rooms.size();

        List<Integer>[] assignRoomsArray = new List[numPeriods];


        for (int i = 0; i < numPeriods; i++) {
            RoomReservation rr = roomReservations.get(i);

            // collect available rooms for gClass
            List<Integer> learningWeeks = rr.getGeneralClass().extractLearningWeeks();
            HashSet<String> occupiedRooms = new HashSet();

            for (RoomOccupation ro : roomOccupations) {
                if (ro.getCrew() != null && rr.getCrew() != null &&
                        ro.getCrew().equals(rr.getCrew()) &&
                        ro.getDayIndex().equals(rr.getWeekday())) {
                    //boolean occupied = false;
                    for (int w : learningWeeks)
                        if (w == ro.getWeekIndex()) {
                            boolean notOverlap = rr.getStartTime() > ro.getEndPeriod()
                                    || ro.getStartPeriod() > rr.getEndTime();
                            if (notOverlap == false) {
                                occupiedRooms.add(ro.getClassRoom());
                            }
                        }
                }
            }


            assignRoomsArray[i] = new ArrayList<Integer>();
            for (int r = 0; r < numRooms; r++) {
                if (!occupiedRooms.contains(rooms.get(r).getClassroom()) &&
                        roomCapacities[r] >= studentQuantities[i]) {
                    assignRoomsArray[i].add(r);
                    log.info("autoScheduleRoom, candidate rooms of Class " + rr.getGeneralClass().getClassCode() + " ADD " + rooms.get(r).getClassroom());
                }
            }

            if (assignRoomsArray[i].isEmpty()) {
                log.error("Không tìm thấy phòng cho lớp " + classes.get(i));
                throw new NotFoundException("Không tìm thấy phòng cho lớp " + classes.get(i));
            }
        }
        List<int[]> C = new ArrayList();
        boolean[][] conflict = new boolean[numPeriods][numPeriods];
        for (int i = 0; i < numPeriods; i++) {
            for (int j = i + 1; j < numPeriods; j++) {

                RoomReservation rr_i = roomReservations.get(i);
                RoomReservation rr_j = roomReservations.get(j);

                List<Integer> LW_i = rr_i.getGeneralClass().extractLearningWeeks();
                List<Integer> LW_j = rr_j.getGeneralClass().extractLearningWeeks();

                if (intersect(LW_i, LW_j) &&
                        rr_i.getCrew() != null && rr_j.getCrew() != null &&
                        rr_i.getCrew().equals(rr_j.getCrew()) &&
                        rr_i.getWeekday() == rr_j.getWeekday()) {
                    boolean notOverlap = rr_i.getStartTime() > rr_j.getEndTime() ||
                            rr_j.getStartTime() > rr_i.getEndTime();
                    if (notOverlap == false) {
                        C.add(new int[]{i, j});
                    }
                }
            }
        }

        /*Check the conflict*/
        /*
        for (GeneralClass gClass : classes) {
            List<RoomReservation> timeSlots = gClass.getTimeSlots();
            for (RoomReservation rr : timeSlots) {
                //Remove all the room of assign rooms to a class if that time have been taken
                if (rr.isTimeSlotNotNull()) {

                    for (RoomOccupation roomOccupation : roomOccupations) {
                        if (
                                rr.getWeekday().equals(roomOccupation.getDayIndex()) &&
                                        rr.getStartTime().equals(roomOccupation.getStartPeriod()) &&
                                        rr.getEndTime().equals(roomOccupation.getEndPeriod()) &&
                                        !rr.getGeneralClass().getClassCode().equals(roomOccupation.getClassCode()) &&
                                        rr.getGeneralClass().getCrew().equals(roomOccupation.getCrew())
                        ) {
                            log.info("START REMOVE FOR: " + rr.getGeneralClass().getClassCode());
                            List<Integer> roomElement = rooms
                                    .stream().filter(room -> room.getClassroom().equals(roomOccupation.getClassRoom()))
                                    .map(rooms::indexOf).toList();
                            //Check if the class of the time slot conflict is in priority rooms list
                            if (!roomElement.isEmpty()) {
                                Integer roomIndex = roomElement.get(0);
                                assignRoomsArray[classes.indexOf(gClass)].remove(roomIndex);
                                log.info(assignRoomsArray[classes.indexOf(gClass)].stream().map(j->rooms.get(j).getClassroom()).toList());
                                break;
                            }
                        }
                    }


                }
                if (rr.getEndTime() != null && rr.getStartTime() > rr.getEndTime())
                    throw new ConflictScheduleException("Thời gian bắt đầu không thể lớn hơn thời gian kết thúc! " + gClass);
                GeneralClass conflictClass = ClassTimeComparator.findClassConflict(rr, gClass, classes);
                if (conflictClass != null) {
                    C.add(new int[]{classes.indexOf(gClass), classes.indexOf(conflictClass)});
                }
            }
        }
        */

        System.out.println("NUMBER OF CLASSES:" + classes.size());

//        for (int i = 0; i < roomCapacities.length; i++) {
//            log.info("ROOM CAP OF " + rooms.get(i).getClassroom() + " : " + roomCapacities[i]);
//        }
//        for (int i = 0; i < studentQuantities.length; i++) {
//            log.info("STUDENT QUANTITY " + roomReservations.get(i).getGeneralClass().getClassCode() + " : " + studentQuantities[i]);
//        }
//        for (int i = 0; i < assignRoomsArray.length; i++) {
//            log.info("CLASSROOM ASSIGN FOR CLASS " + roomReservations.get(i).getGeneralClass().getClassCode() + " : " + assignRoomsArray[i].stream().map(j -> rooms.get(j).getClassroom()).toList());
//        }
//
//        for (int[] p : C) {
//            conflict[p[0]][p[1]] = true;
//        }
//
//        for (int[] p : C) {
//            log.info("CONFLICT " + p[0] + "-" + p[1] + " : " + conflict[p[0]][p[1]]);
//        }



        /*Call the classroom solver*/
        ClassRoomScheduleBacktrackingSolver solver = new ClassRoomScheduleBacktrackingSolver(numPeriods, numRooms, numClasses, scheduleMap, conflict, roomCapacities, assignRoomsArray, timeLimit * 1000);
        solver.solve();
        solver.printSolution();
        roomReservations.forEach(rr -> {
            rr.setRoom(rooms.get(solver.getSolution()[roomReservations.indexOf(rr)]).getClassroom());
        });
        return classes;
    }
}