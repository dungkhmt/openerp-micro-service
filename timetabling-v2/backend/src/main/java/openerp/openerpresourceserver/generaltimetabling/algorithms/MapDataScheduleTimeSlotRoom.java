package openerp.openerpresourceserver.generaltimetabling.algorithms;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MapDataScheduleTimeSlotRoom {
    int[] roomCapacity;
    int nbClassSegments;// number of class-segments
    int[] nbSlots; // nbSlots[i] - duration of i-th segment
    String[] courseCode; // courseCode[i] - course code of i-th segment
    Long[] classId; // classId[i] - class code of i-th segment
    int[] groupId; // groupId[i] - group id of i-th segment
    List<Integer>[] relatedGroupId; // list of group the class-segment involves
    Long[] parentClassId; // parentClassId[i] - id of the parent class (class LT) of the segment i (class BT)
    int[] nbStudents;// nbStudents[i] - number of students of i-th segment
    List<Integer[]> conflict; // conflict[i] = pair [i,j] of 2 class-segments i and j cannot be scheduled in overlapping time durations
    List<Integer>[] domains;// domains[i] is the domain (time-slot) of class-segment i
    List<Integer>[] rooms; // rooms[i] - list of indices of rooms sorted in descendant of priority
    // additional data
    //Map<Integer, GeneralClass> mClassSegment2Class;
}

