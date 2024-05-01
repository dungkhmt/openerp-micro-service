package openerp.openerpresourceserver.generaltimetabling.algorithms;

import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.generaltimetabling.exception.ConflictScheduleException;
import openerp.openerpresourceserver.generaltimetabling.helper.ClassTimeComparator;
import openerp.openerpresourceserver.generaltimetabling.helper.MassExtractor;
import openerp.openerpresourceserver.generaltimetabling.model.entity.Classroom;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.GeneralClassOpened;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.RoomReservation;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class V2ClassScheduler {
    public static List<GeneralClassOpened> autoScheduleTimeSlot(List<GeneralClassOpened> classes) {
        int n = classes.size();
        if (n == 0 ) {
            log.info("Chưa chọn lớp!");
            return null;
        }
        List<int[]> conflict = new ArrayList<int[]>();
        int[] durations = classes.stream()
                .filter(c -> c.getMass() != null)
                .mapToInt(c -> MassExtractor.extract(c.getMass()))
                .toArray();
        ArrayList[] domains = new ArrayList[n];

        for(int i = 0; i< n; i++){
            domains[i] = new ArrayList<>();
            int KIP = classes.get(i).getCrew() == "S" ? 0 : 1;
            for(int day = 0; day < 5; day++){
                for(int start = 1; start <= 6 - durations[i];start++){
                    int s = 12*day+6*KIP + start;
                    domains[i].add(s);
                }
            }
        }
        for(int i = 0; i < n; i++){
            for(int j = i+1; j < n; j++)
                conflict.add(new int[]{i,j});
        }

        ClassTimeScheduleBacktrackingSolver solver =
                new ClassTimeScheduleBacktrackingSolver(n, durations, domains, conflict);
        solver.solve();
        if(!solver.hasSolution()){
            log.error("NO SOLUTION");
        }else {
            int[] solution = solver.getSolution();
            log.info("FOUND SOLUTION");
            for (int i = 0; i < n; i++) {
                int day = solution[i] / 12;
                int t1 = solution[i] - day * 12;
                int K = t1 / 6; // kip
                int tietBD = t1 - 6 * K;
                GeneralClassOpened gClass = classes.get(i);
                gClass.getTimeSlots().forEach(rr -> rr.setGeneralClassOpened(null));
                gClass.getTimeSlots().clear();
                RoomReservation newRoomReservation = new RoomReservation(
                        tietBD ,
                        tietBD + MassExtractor.extract(gClass.getMass())-1,
                        day +2,
                        null) ;
                newRoomReservation.setGeneralClassOpened(gClass);
                gClass.getTimeSlots().add(newRoomReservation);
                log.info("class[" + i + "] is assigned to slot " + solution[i] + "(" + day + "," + K + "," + tietBD + ")");
            }
        }
        return classes;
    }


    public static List<GeneralClassOpened> autoScheduleRoom(List<GeneralClassOpened> classes, List<Classroom> rooms) {
        /*Validate the timeslot assigned*/
        for (GeneralClassOpened gClass : classes) {
            if (gClass.getTimeSlots().isEmpty()) {
                log.error("Lớp " + gClass + " chưa được gán lịch học!");
                return null;
            }
        }

        /*Initial data*/
        int[] roomCapacities = rooms.stream().mapToInt(room -> Math.toIntExact(room.getQuantityMax())).toArray();
        int[] studentQuantities = classes.stream().mapToInt(gClass -> Integer.parseInt(gClass.getQuantity())).toArray();
        int numClasses = classes.size();
        int numRooms = rooms.size();
        List[] assignRoomsArray = new List[numClasses];
        for(int i = 0; i < numClasses; i++){
            assignRoomsArray[i] = new ArrayList<Integer>();
            for(int r = 0;r < numRooms; r++)
                if(roomCapacities[r] >= studentQuantities[i]) assignRoomsArray[i].add(r);
            if (assignRoomsArray[i].isEmpty()) {
                log.error("Không tìm thấy phòng cho lớp " + classes.get(i));
                return null;
            }
        }
        List<int[]> C = new ArrayList();
        boolean[][] conflict = new boolean[numClasses][numClasses];
        /*Check the conflict*/
        for (GeneralClassOpened gClass : classes) {
            List<RoomReservation> timeSlots = gClass.getTimeSlots();
            for (RoomReservation rr : timeSlots) {
                if (rr.getEndTime() != null && rr.getStartTime() > rr.getEndTime()) throw new ConflictScheduleException("Thời gian bắt đầu không thể lớn hơn thời gian kết thúc! " + gClass);
                GeneralClassOpened conflictClass = ClassTimeComparator.findClassConflict(rr, gClass, classes);
                if ( conflictClass != null) {
                    C.add(new int[] {classes.indexOf(gClass), classes.indexOf(conflictClass)});
                }
            }
        }

        for(int[] p: C){
            conflict[p[0]][p[1]] = true;
        }

        /*Call the classroom solver*/
        ClassRoomScheduleBacktrackingSolver solver =
                new ClassRoomScheduleBacktrackingSolver(numClasses, numRooms,conflict, roomCapacities, assignRoomsArray);
        solver.setTimeLimit(2000);// time limit 2 seconds
        solver.solve();
        solver.printSolution();
        return classes;
    }
}
