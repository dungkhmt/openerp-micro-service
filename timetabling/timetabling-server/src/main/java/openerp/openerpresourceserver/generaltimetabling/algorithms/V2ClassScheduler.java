package openerp.openerpresourceserver.generaltimetabling.algorithms;

import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.generaltimetabling.exception.ConflictScheduleException;
import openerp.openerpresourceserver.generaltimetabling.exception.InvalidClassStudentQuantityException;
import openerp.openerpresourceserver.generaltimetabling.exception.NotFoundException;
import openerp.openerpresourceserver.generaltimetabling.helper.ClassTimeComparator;
import openerp.openerpresourceserver.generaltimetabling.helper.MassExtractor;
import openerp.openerpresourceserver.generaltimetabling.model.entity.Classroom;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.GeneralClass;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.RoomReservation;
import openerp.openerpresourceserver.generaltimetabling.model.entity.occupation.RoomOccupation;
import openerp.openerpresourceserver.generaltimetabling.service.RoomOccupationService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Log4j2
public class V2ClassScheduler {

    //private RoomOccupationService roomOccupationService;

    public static boolean intersect(List<Integer> L1, List<Integer> L2){
        for(int i: L1){
            for(int j: L2){
                if(i == j) return true;
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
        List<int[]> conflict = new ArrayList<int[]>();
        //int[] durations = classes.stream().filter(c -> c.getMass() != null).mapToInt(c -> MassExtractor.extract(c.getMass())).toArray();
        int[] durations = new int[n];
        for(int i = 0; i < classes.size(); i++){
            durations[i] = classes.get(i).getDuration();
        }
        ArrayList[] domains = new ArrayList[n];

        for (int i = 0; i < n; i++) {
            domains[i] = new ArrayList<>();
            GeneralClass c = classes.get(i);
            boolean fixedTimeSlot = false;
            if(c.getTimeSlots() != null && c.getTimeSlots().size() > 0){
                RoomReservation rr = c.getTimeSlots().get(0);
                int start = -1;
                int end = -1;
                int day = -1;
                int KIP = -1;
                if(rr.getStartTime() != null && rr.getStartTime() > 0){
                    start = rr.getStartTime();
                }
                if(rr.getEndTime() != null && rr.getEndTime() > 0){
                    end = rr.getEndTime();
                }
                if(rr.getWeekday() != null && rr.getWeekday() > 0){
                    day = rr.getWeekday() - 2;// 0 means Monday, 1 means Tuesday...
                }
                if(rr.getCrew()!= null){
                    if(rr.getCrew().equals("S")) KIP = 0;
                    else KIP = 1;
                }
                if(start > 0 && end >0 && day > 0){
                    fixedTimeSlot = true;
                    int s = 12 * day + 6 * KIP + start;
                    domains[i].add(s);// time-slot is assigned in advance
                }
            }

            if(!fixedTimeSlot) {// the class is not assigned time-slot yet
                int KIP = classes.get(i).getCrew() == "S" ? 0 : 1;
                for (int day = 0; day < 5; day++) {
                    for (int start = 1; start <= 6 - durations[i]; start++) {
                        int s = 12 * day + 6 * KIP + start;
                        domains[i].add(s);
                    }
                }
            }
        }
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                GeneralClass ci = classes.get(i);
                GeneralClass cj = classes.get(j);
                if(ci.getRefClassId().equals(cj.getRefClassId())){
                    if(ci.getClassCode().equals(cj.getClassCode())){// same classCode means 2 segments of the same class
                        conflict.add(new int[]{i, j});
                    }else {// different classCode, then add conflict is parent-child classes
                        if (ci.getParentClassId().equals(cj.getId()) ||
                                cj.getParentClassId().equals(ci.getId())) {
                            conflict.add(new int[]{i, j});
                        }
                    }

                }else{
                    conflict.add(new int[]{i, j});
                }
                //conflict.add(new int[]{i, j});
            }
        }

        ClassTimeScheduleBacktrackingSolver solver = new ClassTimeScheduleBacktrackingSolver(n, durations, domains, conflict, timeLimit);
        solver.solve();
        if (!solver.hasSolution()) {
            log.error("NO SOLUTION");
            throw new NotFoundException("Không tìm thấy cách xếp các lớp học!");
        } else {
            int[] solution = solver.getSolution();
            log.info("FOUND SOLUTION");
            for (int i = 0; i < n; i++) {
                int day = solution[i] / 12;
                int t1 = solution[i] - day * 12;
                int K = t1 / 6; // kip
                int tietBD = t1 - 6 * K;
                GeneralClass gClass = classes.get(i);
                gClass.setStartTime(tietBD);
                gClass.setEndTime(tietBD + gClass.getDuration() - 1);
                gClass.setWeekday(day + 2);
                gClass.getTimeSlots().forEach(rr -> rr.setGeneralClass(null));
                gClass.getTimeSlots().clear();
                //RoomReservation newRoomReservation = new RoomReservation(gClass.getCrew(),tietBD, tietBD + MassExtractor.extract(gClass.getMass()) - 1, day + 2, null);
                RoomReservation newRoomReservation = new RoomReservation(gClass.getCrew(),tietBD, tietBD + gClass.getDuration() - 1, day + 2, null);

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

        /*Initial data*/
        int[] roomCapacities = rooms.stream().mapToInt(room -> Math.toIntExact(room.getQuantityMax())).toArray();
        int[] studentQuantities = classes.stream().mapToInt(gClass -> {
            if (gClass.getQuantityMax() == null || gClass.getQuantityMax().isEmpty())
                throw new InvalidClassStudentQuantityException(gClass.getClassCode() + " đang không có số học sinh tối đa!");
            return Integer.parseInt(gClass.getQuantityMax());
        }).toArray();
        int numClasses = classes.size();
        int numRooms = rooms.size();
        List<Integer>[] assignRoomsArray = new List[numClasses];
        for (int i = 0; i < numClasses; i++) {
            GeneralClass gClass = classes.get(i);
            if(gClass.getTimeSlots() == null || gClass.getTimeSlots().size() != 1){
                log.error("Class " + gClass.getClassCode() + " does not have exactly 1 time-slot-room");
                return null;
            }
            // collect available rooms for gClass
            RoomReservation rr = gClass.getTimeSlots().get(0);
            List<Integer> learningWeeks = gClass.extractLearningWeeks();
            HashSet<String> occupiedRooms = new HashSet();
            for(RoomOccupation ro: roomOccupations){
                if(ro.getCrew() != null && rr.getCrew() != null &&
                        ro.getCrew().equals(rr.getCrew()) &&
                        ro.getDayIndex() == rr.getWeekday()){
                    //boolean occupied = false;
                    for(int w: learningWeeks)if(w == ro.getWeekIndex()){
                        boolean notOverlap = rr.getStartTime() > ro.getEndPeriod()
                                || ro.getStartPeriod() > rr.getEndTime();
                        if(notOverlap == false){
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
                    log.info("autoScheduleRoom, candidate rooms of Class " + gClass.getClassCode() + " ADD " + rooms.get(r).getClassroom());
                }
            }
            if (assignRoomsArray[i].isEmpty()) {
                log.error("Không tìm thấy phòng cho lớp " + classes.get(i));
                return null;
            }
        }
        List<int[]> C = new ArrayList();
        boolean[][] conflict = new boolean[numClasses][numClasses];
        for(int i = 0; i < numClasses; i++){
            for(int j = i+1; j < numClasses; j++){
                GeneralClass ci = classes.get(i);
                GeneralClass cj = classes.get(j);
                List<Integer> LW_i = ci.extractLearningWeeks();
                List<Integer> LW_j = cj.extractLearningWeeks();

                RoomReservation rr_i = ci.getTimeSlots().get(0);
                RoomReservation rr_j = cj.getTimeSlots().get(0);
                if(intersect(LW_i,LW_j) &&
                        rr_i.getCrew() != null && rr_j.getCrew() != null &&
                        rr_i.getCrew().equals(rr_j.getCrew()) &&
                        rr_i.getWeekday() == rr_j.getWeekday()){
                    boolean notOverlap = rr_i.getStartTime() > rr_j.getEndTime() ||
                            rr_j.getStartTime() > rr_i.getEndTime();
                    if(notOverlap == false){
                        C.add(new int[]{i,j});
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

        for (int i = 0; i < roomCapacities.length; i++) {
            log.info("ROOM CAP OF " + rooms.get(i).getClassroom() + " : " + roomCapacities[i]);
        }
        for (int i = 0; i < studentQuantities.length; i++) {
            log.info("STUDENT QUANTITY " + classes.get(i).getClassCode() + " : " + studentQuantities[i]);
        }
        for (int i = 0; i < assignRoomsArray.length; i++) {
            log.info("CLASSROOM ASSIGN FOR CLASS " + classes.get(i).getClassCode() + " : " + assignRoomsArray[i].stream().map(j->rooms.get(j).getClassroom()).toList());
        }

        for (int[] p : C) {
            conflict[p[0]][p[1]] = true;
        }

        for (int[] p : C) {
            log.info("CONFLICT " + p[0] + "-" + p[1] + " : " + conflict[p[0]][p[1]]);
        }



        /*Call the classroom solver*/
        ClassRoomScheduleBacktrackingSolver solver = new ClassRoomScheduleBacktrackingSolver(numClasses, numRooms, conflict, roomCapacities, assignRoomsArray, timeLimit * 1000);
        solver.solve();
        solver.printSolution();
        classes.forEach(gClass -> {
            List<RoomReservation> timeSlots = gClass.getTimeSlots();
            for (RoomReservation rr : timeSlots) {
                rr.setRoom(rooms.get(solver.getSolution()[classes.indexOf(gClass)]).getClassroom());
            }
        });
        return classes;
    }
}