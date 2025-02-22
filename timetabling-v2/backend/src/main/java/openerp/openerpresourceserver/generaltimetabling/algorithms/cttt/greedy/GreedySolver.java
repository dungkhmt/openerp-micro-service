package openerp.openerpresourceserver.generaltimetabling.algorithms.cttt.greedy;

import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.generaltimetabling.algorithms.MapDataScheduleTimeSlotRoom;
import openerp.openerpresourceserver.generaltimetabling.algorithms.MapDataScheduleTimeSlotRoomOneGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

@Log4j2
public class GreedySolver {
    MapDataScheduleTimeSlotRoom I;
    int[] solutionSlot;// solutionSlot[i] is the start time-slot assigned to class-segment i
    int[] solutionRoom; // solutionRoom[i] is the room assigned to class-segment i
    HashSet<Integer>[] conflictClassSegment;// conflictClassSegment[i] is the list of class-segment conflict with class segment i
    List<Integer> unScheduledClassSegment;
    boolean foundSolution;
    public GreedySolver(MapDataScheduleTimeSlotRoom I){

        this.I = I;
        conflictClassSegment = new HashSet[I.getNbClassSegments()];
        for(int i = 0; i < I.getNbClassSegments(); i++){
            conflictClassSegment[i] = new HashSet();
        }
        for(Integer[] p: I.getConflict()){
            int i = p[0]; int j = p[1];
            conflictClassSegment[i].add(j); conflictClassSegment[j].add(i);
        }
    }

    private boolean overLap(int startSlot1, int duration1, int startSlot2, int duration2){
        if(startSlot1 + duration1 <= startSlot2 || startSlot2 + duration2 <= startSlot1) return false;
        return true;
    }
    private boolean check(int i, int s, int r){
        // check and return true if slot s and room r can be assigned to class segment i without violating constraintsa
        // explore all class segment j before i (have been assigned slot and room)
        int duration_i = I.getNbSlots()[i];
        int startSlot_i = s;

        for(int j = 0; j <= i-1; j++){
            int duration_j = I.getNbSlots()[j];
            int startSlot_j = solutionSlot[j];
            if(i == 4)log.info("check(" + i + "," + s + "," + r + " compare class-segment " + j + " having start_slot_j = " + startSlot_j + " duration_j = " + duration_j + " room " + solutionRoom[j]);
            if(conflictClassSegment[i].contains(j)){// class segments i and j conflict
                if(overLap(startSlot_i,duration_i,startSlot_j,duration_j))
                    return false;
            }
            if(overLap(startSlot_i, duration_i,startSlot_j,duration_j)){
                if(solutionRoom[j] == r) return false;
            }
        }
        return true;
    }
    public int[] getSolutionSlot(){ return solutionSlot;}
    public int[] getSolutionRoom(){ return solutionRoom;}

    public void greedy2(){
        // TO DO by Chau
    }
    public void greedy3(){
        // TODO by Chau
    }
    public void simpleGreedy(){
        unScheduledClassSegment = new ArrayList<>();
        for(int i = 0; i < I.getNbClassSegments(); i++){
            // try to find a first-fit time-slot and room for class segment i
            log.info("simpleGreedy, start plan for class-segment " + i + ": list time-slots = " + I.getDomains()[i]);
            boolean foundSlotRoom = false;
            for(int s: I.getDomains()[i]){
                if(foundSlotRoom) break;
                for(int r: I.getRooms()[i]){
                    if(foundSlotRoom) break;
                    if(check(i,s,r)){
                        solutionSlot[i]= s; solutionRoom[i] = r; foundSlotRoom = true;
                        log.info("simpleGreedy, slot[" + i + "] = " + s + ", duration = " + I.getNbSlots()[i] + ", room[" + i + "] = " + r);
                    }
                }
            }
            if(!foundSlotRoom){
                unScheduledClassSegment.add(i);
            }
        }
        foundSolution = unScheduledClassSegment.size() == 0;
    }
    public boolean hasSolution(){
        return foundSolution;
    }
    public void solve(){
        for(int i = 0; i < I.getNbClassSegments(); i++){
            System.out.println("class-segment " + i + ": nbSlot = " + I.getNbSlots()[i] + ", nbStudents = " + I.getNbStudents()[i]);
            System.out.print("list of start time-slot for class-segment " + i + ": ");
            for(int s: I.getDomains()[i]) System.out.print(s + ", ");
            System.out.println();
            System.out.print("list of rooms can be assigned to class-segment " + i + ": ");
            for(int r: I.getRooms()[i]) System.out.print(r + ", ");
            System.out.println();
        }
        for(Integer[] p: I.getConflict()){
            System.out.println("Conflict between class-segment " + p[0] + " and " + p[1]);
        }
        solutionSlot = new int[I.getNbClassSegments()];
        solutionRoom = new int[I.getNbClassSegments()];
        for(int i = 0; i < I.getNbClassSegments(); i++){
            solutionRoom[i] = -1; solutionSlot[i] = -1; // NOT ASSIGNED/SCHEDULED
        }
        simpleGreedy();
        printSolution();
    }
    public void printSolution(){
        System.out.print("Unschedueld class-segments: ");
        for(int i: unScheduledClassSegment) System.out.print(i + ", ");
        System.out.println();
        for(int i = 0; i < I.getNbClassSegments(); i++){
            if(solutionSlot[i] > -1){
                System.out.println("class-segment[" + i + "] slot = " + solutionSlot[i] + " room = " + solutionRoom[i]);
            }
        }
    }
    public static void main(String[] args){
        try{
            Gson gson = new Gson();
            Scanner in = new Scanner(new File("timetable.json"));
            String json = in.nextLine();
            in.close();
            MapDataScheduleTimeSlotRoom I = gson.fromJson(json,MapDataScheduleTimeSlotRoom.class);
            GreedySolver solver = new GreedySolver(I);
            solver.solve();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
