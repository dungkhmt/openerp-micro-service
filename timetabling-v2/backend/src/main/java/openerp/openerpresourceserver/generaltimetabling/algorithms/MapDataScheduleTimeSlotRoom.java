package openerp.openerpresourceserver.generaltimetabling.algorithms;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.GeneralClass;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MapDataScheduleTimeSlotRoom {
    int[] roomCapacity;
    int nbClassSegments;// number of class-segments, numbered from 0, 1, 2....
    int[] nbSlots; // d[i] is the duration (so tiet), nbSlots[i] : slots class-segment i
    String[] courseCode; // c[i] is the course of the class-segment i
    Long[] classId; // cls[i]  is the class id of the class-segment i
    int[] groupId; // id of group (KHMT 1st year), 0, 1, 2, ...
    Long[] parentClassId; // parentClassId[i] id of the parent class (class LT) of the class i (class BT)
    int[] nbStudents;// vol[i] is the number of students of class-segment i
    List<Integer[]> conflict; // conflict[i] = pair [i,j] of 2 class-segments i and j cannot be scheduled in overlapping time durations
    List<Integer>[] domains;// domains[i] is the domain (time-slot) of class-segment i
    List<Integer>[] rooms; // rooms[i]: list of indices of rooms sorted in descendant of priority
    // additional data
    //Map<Integer, GeneralClass> mClassSegment2Class;
}

