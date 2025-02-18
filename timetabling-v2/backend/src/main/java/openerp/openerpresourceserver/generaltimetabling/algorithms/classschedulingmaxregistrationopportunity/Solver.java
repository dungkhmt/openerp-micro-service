package openerp.openerpresourceserver.generaltimetabling.algorithms.classschedulingmaxregistrationopportunity;

import openerp.openerpresourceserver.generaltimetabling.algorithms.MapDataScheduleTimeSlotRoomOneGroup;
import com.google.gson.Gson;

import java.io.File;
import java.util.List;
import java.util.Scanner;

public class Solver {
    // input data structures
    MapDataScheduleTimeSlotRoomOneGroup data;
    int nbClassSegments;// number of class-segments
    int[] nbSlots; // d[i] is the duration (so tiet)
    String[] courseCode; // c[i] is the course of the class-segment i
    Long[] classId; // cls[i]  is the class id of the class-segment i
    Long[] parentClassId; // parentClassId[i] id of the parent class (class LT) of the class i (class BT)
    int[] nbStudents;// vol[i] is the number of students of class-segment i
    boolean[][] conflict; // conflict[i][j] = true means that class-segment i and j cannot be scheduled in overlapping time durations
    List<Integer>[] domains;// D[i] is the domain of class-segment i
    //List<Integer> rooms; // list of indices of rooms sorted in descendant of priority
    int nbRooms;// number of rooms 0,1,2,... decreasing order of priority (0: highest priority, 1: second-highest priority...)

    int[] roomCap; // cap[i] is the capacity of room i, length(cap) = m

    // data structures for the search defined here
    int[] solution; // solution[i] is the start time-slot of class-segment i


    public Solver(MapDataScheduleTimeSlotRoomOneGroup data){
        this.data = data;
        this.nbClassSegments = data.getNbClassSegments();
        this.nbSlots = data.getNbSlots();
        this.courseCode = data.getCourseCode();
        this.classId= data.getClassId();
        this.parentClassId = data.getParentClassId();
        this.nbStudents = data.getNbStudents();
        this.conflict = data.getConflict();
        this.domains = data.getDomains();
        //this.nbRooms = data.getNbRooms();
        //this.roomCap = data.getRoomCap();
    }
    public void solve(){
        // to be implemented
    }
    public static void main(String[] args){
        try{
            MapDataScheduleTimeSlotRoomOneGroup data = new MapDataScheduleTimeSlotRoomOneGroup();
            Gson gson = new Gson();
            Scanner in = new Scanner(new File("timetable.json"));
            String json = in.nextLine();
            System.out.println("read json = " + json);
            data = gson.fromJson(json, MapDataScheduleTimeSlotRoomOneGroup.class);
            in.close();
            data.print();
            Solver app = new Solver(data);
            app.solve();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
