package openerp.openerpresourceserver.generaltimetabling.algorithms;

import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.generaltimetabling.model.Constant;

import java.util.*;
@Log4j2
public class Util {
    public static List<Integer> generateSLotSequence(String crew, int duration){
        int startCrew = 0; int endCrew = 1;
        if(crew != null){
            int fKIP = crew.equals("S") ? 0 : 1;
            startCrew = fKIP; endCrew = fKIP;
        }
        //log.info("generateSLotSequence, crew = " + crew + " start_crew = " + startCrew + " end_crew = " + endCrew);
        List<Integer> res = new ArrayList<>();
        for(int d = 0; d < Constant.daysPerWeek; d++) {
            for(int fKIP = startCrew; fKIP <= endCrew; fKIP++){
                for(int s = 1; s <= Constant.slotPerCrew; s++)if(s + duration - 1 <= Constant.slotPerCrew){
                    int start = Constant.slotPerCrew * 2 * d
                            + Constant.slotPerCrew * fKIP + s;
                    res.add(start);
                }
            }
        }
        return res;
    }
    public static List<Integer> toIntList(String s, int duration){
        List<Integer> res = new ArrayList<>();
        if(s != null){
            String[] as = s.split(",");
            if(as != null && as.length > 0){
                for(String si: as){
                    try{
                        int x = Integer.valueOf(si);
                        if(x + duration - 1 <= Constant.slotPerCrew) res.add(x);
                    }catch (Exception e){
                        return res;
                    }
                }
            }
        }
        return res;
    }
    public static List<Integer> generateSlots(String daySeq, String slotSeq, String crew, int duration){
        int startCrew = 0; int endCrew = 1;
        if(crew != null){
            int fKIP = crew.equals("S") ? 0 : 1;
            startCrew = fKIP; endCrew = fKIP;
        }
        //if(daySeq == null || slotSeq == null) return null;

        //if(sd == null || sd.length == 0) return null;
        Set<Integer> allDays = new HashSet();
        Set<Integer> allSlots = new HashSet();
        for(int d = 2; d <= 8; d++) allDays.add(d);
        for(int sl = 1; sl <= Constant.slotPerCrew;sl++) allSlots.add(sl);

        List<Integer> days = new ArrayList();
        if(daySeq != null) {
            String[] sd = daySeq.split(",");
            if(sd != null && sd.length > 0) {
                for (int i = 0; i < sd.length; i++) {
                    int d = Integer.valueOf(sd[i]);
                    days.add(d);
                }
            }
        }
        for(int di: allDays){
            if(!days.contains(di)) days.add(di);
        }
        List<Integer> slots = new ArrayList();
        if(slotSeq != null) {
            String[] ss = slotSeq.split(",");
            if (ss != null && ss.length > 0) {
                for (int i = 0; i < ss.length; i++) {
                    int sl = Integer.valueOf(ss[i]);
                    slots.add(sl);
                }
            }
        }
        //log.info("mapData, slots = " + slots);
        for(int si: allSlots){
            if(!slots.contains(si)) slots.add(si);
        }
        //log.info("mapData, allSlots = "  + allSlots + " -> slots = " + slots + ", days = " + days);

        List<Integer> res = new ArrayList<>();
        for(int d: days){
            for(int fKIP = startCrew; fKIP <= endCrew; fKIP++) {
                for (int s : slots) if(s + duration - 1 <= Constant.slotPerCrew){

                    int start = Constant.slotPerCrew * 2 * (d - 2)
                            + Constant.slotPerCrew * fKIP + s;

                    //log.info("mapData,d = " + d + ", s = " + s + ", duration = " + duration + " -> start = " + start);
                    res.add(start);
                }
            }
        }

        return res;
    }
    public static List<Integer> shift(List<Integer> L, List<Integer> LP){
        // put sequence of LP elements at the beginning of L,
        // keep relative order of elements of L and LP in the resulting list
        //int[] a = new int[L.size() + LP.size()];
        //Set<Integer> S = new HashSet();
        List<Integer> res = new ArrayList();
        //int idx = -1;
        for(int i: LP){
            res.add(i);
            //idx++; a[idx] = i;
        }
        for(int i: L){
            if(!LP.contains(i)){
                //idx++; a[idx] = i;
                res.add(i);
            }
        }

        //for(int i = 0; i < a.length; i++) res.add(a[i]);
        return res;
    }
    public static void main(String[] args) {
        List<Integer> L = Arrays.asList(new Integer[]{2,3,4,3,5,6,7,2,3,9});
        List<Integer> LP = Arrays.asList(new Integer[]{7,4,9});
        List<Integer> res = Util.shift(L,LP);
        System.out.println("res = " + res);
    }
}
