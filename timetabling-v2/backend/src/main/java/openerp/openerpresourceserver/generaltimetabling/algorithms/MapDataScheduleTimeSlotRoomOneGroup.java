package openerp.openerpresourceserver.generaltimetabling.algorithms;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.GeneralClass;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Log4j2
public   class MapDataScheduleTimeSlotRoomOneGroup {

    int nbClassSegments;// number of class-segments
    int[] nbSlots; // d[i] is the duration (so tiet)
    String[] courseCode; // c[i] is the course of the class-segment i
    Long[] classId; // cls[i]  is the class id of the class-segment i
    int groupId; // id of group (KHMT 1st year)
    Long[] parentClassId; // parentClassId[i] id of the parent class (class LT) of the class i (class BT)
    int[] nbStudents;// vol[i] is the number of students of class-segment i
    boolean[][] conflict; // conflict[i][j] = true means that class-segment i and j cannot be scheduled in overlapping time durations
    List<Integer>[] domains;// D[i] is the domain of class-segment i
    //int[] roomCap; // cap[i] is the capacity of room i, length(cap) = m
    List<Integer> rooms; // list of indices of rooms sorted in descendant of priority
    //int nbRooms;// number of rooms 0,1,2,... decreasing order of priority (0: highest priority, 1: second-highest priority...)

    //int[] p; // priority of room i (high value of p[i] = high priority to be used)

    // additional data
    Map<Integer, GeneralClass> mClassSegment2Class;


    public void print(){
        for(int i = 0; i < nbClassSegments; i++){
            String sDomain = "";
            for(int v: domains[i]) sDomain = sDomain + v + ",";
            log.info("print, class-segment[" + i + "]: nbSLots = " + nbSlots[i] + ", nbStudent = " + nbStudents[i] + ", class = " + classId[i] + ", parent-class = " + parentClassId[i] + ", course " + courseCode[i] + ", Domain = " + sDomain);
        }
        for(int i = 0; i < nbClassSegments; i++){
            for(int j = i+1; j < nbClassSegments; j++){
                if(conflict[i][j]) log.info("conflict " + i + "," + j);
            }
        }
    }
}
