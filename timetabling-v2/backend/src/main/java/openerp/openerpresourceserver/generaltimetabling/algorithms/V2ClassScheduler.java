package openerp.openerpresourceserver.generaltimetabling.algorithms;

import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;

import openerp.openerpresourceserver.generaltimetabling.algorithms.classschedulingmaxregistrationopportunity.CourseNotOverlapBackTrackingSolver;
import openerp.openerpresourceserver.generaltimetabling.algorithms.cttt.greedy.GreedySolver;

import openerp.openerpresourceserver.generaltimetabling.exception.InvalidClassStudentQuantityException;
import openerp.openerpresourceserver.generaltimetabling.exception.InvalidFieldException;
import openerp.openerpresourceserver.generaltimetabling.exception.NotFoundException;
import openerp.openerpresourceserver.generaltimetabling.helper.MassExtractor;
import openerp.openerpresourceserver.generaltimetabling.model.Constant;
import openerp.openerpresourceserver.generaltimetabling.model.entity.ClassGroup;
import openerp.openerpresourceserver.generaltimetabling.model.entity.Classroom;
import openerp.openerpresourceserver.generaltimetabling.model.entity.Group;
import openerp.openerpresourceserver.generaltimetabling.model.entity.TimeTablingCourse;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.GeneralClass;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.RoomReservation;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.TimeTablingRoom;
import openerp.openerpresourceserver.generaltimetabling.model.entity.occupation.RoomOccupation;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
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



    public MapDataScheduleTimeSlotRoomWrapper mapData(List<GeneralClass> classes, List<Classroom> rooms,List<TimeTablingCourse> ttcourses, List<Group> groups,List<ClassGroup> classGroups){

            int n = 0;
            Map<Integer, GeneralClass> mClassSegment2Class = new HashMap();
            for (int i = 0; i < classes.size(); i++) {
                GeneralClass gc = classes.get(i);
                if (gc.getTimeSlots() != null) {
                    n += gc.getTimeSlots().size();
                }
            }
            Map<Long, Group> mId2Group = new HashMap<>();
            for(Group g: groups){
                mId2Group.put(g.getId(),g);
            }
            Map<Long, List<Long>> mClassId2ListGroupIds = new HashMap<>();
            for(ClassGroup cg: classGroups){
                if(mClassId2ListGroupIds.get(cg.getClassId())==null){
                    mClassId2ListGroupIds.put(cg.getClassId(),new ArrayList<>());
                    mClassId2ListGroupIds.get(cg.getClassId()).add(cg.getGroupId());
                }
            }
            int groupIdx = -1;
            /*
            Map<String, Integer> mGroupName2Index = new HashMap();
            for(int i = 0; i < classes.size(); i++){
                GeneralClass gc = classes.get(i);
                String group = gc.getGroupName();
                log.info("mapData, group " + group);
                if(mGroupName2Index.get(group)==null){
                    groupIdx++;
                    mGroupName2Index.put(group,groupIdx);
                    log.info("mapData, put(" + group + "," + groupIdx);
                }
            }
             */
            Map<Long, Integer> mGroupId2GroupIndex = new HashMap<>();
            Map<Long, List<Integer>> mClassId2GroupIndex = new HashMap();
            for(ClassGroup cg: classGroups){
                if(mClassId2GroupIndex.get(cg.getClassId())==null)
                    mClassId2GroupIndex.put(cg.getClassId(),new ArrayList<>());
                Group gr = mId2Group.get(cg.getGroupId());
                if(gr != null) {
                    //log.info("mapData, gr.GroupName = " + gr.getGroupName());
                    //if(mGroupName2Index.get(gr.getGroupName())==null)
                    //    log.info("mapData BUT dictionary got " + gr.getGroupName() + " null");
                    //int idxG = mGroupName2Index.get(gr.getGroupName());
                    if(mGroupId2GroupIndex.get(cg.getGroupId())==null){
                        groupIdx+=1;
                        mGroupId2GroupIndex.put(cg.getGroupId(),groupIdx);
                    }
                    //mClassId2GroupIndex.get(cg.getClassId()).add(idxG);
                    mClassId2GroupIndex.get(cg.getClassId()).add(mGroupId2GroupIndex.get(cg.getGroupId()));
                }
            }
            List<Integer>[] relatedGroups = new ArrayList[n];
            int[] indexOfClass = new int[n];// indexOfClass[j] : index of the class of the class-segment (RoomReservation) j
            int[] d = new int[n];
            String[] c = new String[n];
            Long[] cls = new Long[n];
            Long[] parentClassId = new Long[n];
            int[] vol = new int[n];
            int[] groupId = new int[n];
        List<Integer>[] roomPriority = new ArrayList[n];
            //boolean[][] conflict = new boolean[n][n];
        List<Integer[]> conflict = new ArrayList();
        List<Integer>[] D = new List[n];
            for (int i = 0; i < n; i++) D[i] = new ArrayList<Integer>();
            int idx = -1;
            //int[] days = {0, 2, 4, 1, 3};
            int[] days = {0, 1, 2, 3, 4, 5};
            Map<String, Group> mName2Group = new HashMap();
            for(Group g: groups){
                mName2Group.put(g.getGroupName(),g);
            }
            Map<String, TimeTablingCourse> mId2Course = new HashMap();
            for(TimeTablingCourse course: ttcourses){
                mId2Course.put(course.getId(),course);
            }
            for (int i = 0; i < classes.size(); i++) {
                GeneralClass gc = classes.get(i);
                Group gr = mName2Group.get(gc.getGroupName());

                //log.info("mapData, gc[" + i + "] crew = " + gc.getCrew());
                if (gc.getTimeSlots() != null) {
                    for (int j = 0; j < gc.getTimeSlots().size(); j++) {
                        RoomReservation rr = gc.getTimeSlots().get(j);
                        idx++; // new class-segment (RoomReservation)
                        mClassSegment2Class.put(idx, gc);
                        indexOfClass[idx] = i;
                        relatedGroups[idx] = mClassId2GroupIndex.get(gc.getId());

                        d[idx] = rr.getDuration();//gc.getDuration();//gc.getQuantityMax();
                        c[idx] = gc.getCourse();
                        cls[idx] = gc.getId();//gc.getClassCode();
                        vol[idx] = 0;
                        if(gc.getQuantityMax() != null) vol[idx] = gc.getQuantityMax();
                        groupId[idx] = 0;// not used, use relatedGroups instead //mGroupName2Index.get(gc.getGroupName());
                        if(relatedGroups[idx].size() > 0) groupId[idx] = relatedGroups[idx].get(0);
                        parentClassId[idx] = gc.getParentClassId();
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
                            int s = Constant.slotPerCrew * day + Constant.slotPerCrew * KIP + start;
                            D[idx].add(s);// time-slot is assigned in advance
                        } else {
                            if(gr != null){
                                List<Integer> L = Util.generateSlots(gr.getDaySeq(),gr.getSlotSeq(),gc.getCrew(),d[i]);
                                //log.info("mapData, compute D[" + i + "], daySeq = " + gr.getDaySeq() + ", slotSeq = " + gr.getSlotSeq() + ", crew = " + gc.getCrew() + " got L= " + L);
                                TimeTablingCourse crs = mId2Course.get(gc.getCourse());
                                List<Integer> LP = new ArrayList<>();
                                if(crs != null){
                                    LP = Util.toIntList(crs.getSlotPriority(),d[i]);
                                }
                                D[i] = Util.shift(L,LP);
                                //log.info("mapData, compute D[" + i + "], LP = " + LP +", D = " + D);
                            }else{
                                D[i] = Util.generateSLotSequence(gc.getCrew(),d[i]);
                            }
                            //log.info("mapData, class-segment[" + i + "] of class " + gc.getClassCode() + " of course " + gc.getCourse() + " has Domain " + D[i]);
                            /*
                            int fKIP = gc.getCrew().equals("S") ? 0 : 1;
                            for (int fday : days) {
                                log.info("fKIP = " + fKIP + " fday =  + fday");
                                for (int fstart = 1; fstart <= Constant.slotPerCrew - d[idx] + 1; fstart++) {
                                    int s = Constant.slotPerCrew*2 * fday + Constant.slotPerCrew * fKIP + fstart;
                                    D[idx].add(s);
                                    //log.info("mapData, D[" + idx + "].add(" + s + ")");
                                }
                            }
                            */
                        }
                    }
                }
            }

            for (int i = 0; i < n; i++) {
                for (int j = i+1; j < n; j++) {
                    boolean hasConflict = false;
                    if (cls[i] == cls[j]) {
                        //conflict.add(new Integer[]{i,j});
                        hasConflict = true;
                    }
                    GeneralClass ci = classes.get(indexOfClass[i]);
                    GeneralClass cj = classes.get(indexOfClass[j]);
                    if (ci.getId().equals(cj.getParentClassId()) ||
                            cj.getId().equals(ci.getParentClassId())
                    ) {
                        //conflict.add(new Integer[]{i,j});
                        hasConflict = true;
                    }
                    if(groupId[i] == groupId[j]) hasConflict = true;
                    if(hasConflict)conflict.add(new Integer[]{i,j});
                }
            }
            //MapDataScheduleTimeSlotRoomOneGroup data = new MapDataScheduleTimeSlotRoomOneGroup(n,d,c,cls,parentClassId,vol,conflict,D,0,null,mClassSegment2Class);
            //MapDataScheduleTimeSlotRoomOneGroup aGroup = new MapDataScheduleTimeSlotRoomOneGroup(n, d, c, cls, -1, parentClassId, vol, conflict, D, null, null);
        int[] roomCapacity = new int[rooms.size()];
        for(int i = 0; i < rooms.size(); i++) {
            Long cap = rooms.get(i).getQuantityMax();
            roomCapacity[i] = (int) cap.intValue();
        }
        for(int i = 0; i < n; i++){
            roomPriority[i] = new ArrayList<>();
            for(int r = 0; r < roomCapacity.length; r++)
                roomPriority[i].add(r);
        }
        //MapDataScheduleTimeSlotRoom data = new MapDataScheduleTimeSlotRoom(roomCapacity,n,d,c,cls,groupId,parentClassId,vol,conflict,D,roomPriority,mClassSegment2Class);
        MapDataScheduleTimeSlotRoom data = new MapDataScheduleTimeSlotRoom(roomCapacity,n,d,c,cls,groupId,relatedGroups,parentClassId,vol,conflict,D,roomPriority);
        MapDataScheduleTimeSlotRoomWrapper DW = new MapDataScheduleTimeSlotRoomWrapper(data,mClassSegment2Class);

        //data.print();
        return DW;
    }
    public List<GeneralClass> autoScheduleTimeSlotRoom(List<GeneralClass> classes, List<Classroom> rooms, List<TimeTablingCourse> ttcourses, List<Group> groups, List<ClassGroup> classGroups, int timeLimit) {
        log.info("autoScheduleTimeSlotRoom, START....");
        //for(int i = 0; i < rooms.size(); i++){
        //    log.info("autoScheduleTimeSlotRoom, room[" + i + "] = " + rooms.get(i).getClassroom());
        //}
        MapDataScheduleTimeSlotRoomWrapper D = mapData(classes, rooms,ttcourses,groups,classGroups);
        MapDataScheduleTimeSlotRoom data = D.data;
        //data.print();
        Gson gson = new Gson();
        String json = gson.toJson(data);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(now);


        try {
            PrintWriter out = new PrintWriter("timetable-" + data.getNbClassSegments() + "-cls-" + timeStamp + ".json");
            out.print(json);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        HashSet<String> courses = new HashSet();
        Map<Integer, String> mClassSegment2Course = new HashMap();
        Map<String, List<Integer>> mCourse2Domain = new HashMap();
        Map<String, Integer> mCourse2Duration = new HashMap();
        for (int i = 0; i < data.nbClassSegments; i++) {
            String courseCode = data.getCourseCode()[i];
            courses.add(courseCode);
            mClassSegment2Course.put(i, courseCode);
            mCourse2Domain.put(courseCode, data.getDomains()[i]);
            mCourse2Duration.put(courseCode, data.nbSlots[i]);
        }
        //CourseNotOverlapBackTrackingSolver solver = new CourseNotOverlapBackTrackingSolver(courses,mCourse2Domain,mCourse2Duration);
        GreedySolver solver = new GreedySolver(data);
        solver.solve();
        if (!solver.hasSolution()) {
            log.info("autoScheduleTimeSlotRoom, no solution!!!");
        } else {
            //Map<String, Integer> solutionMap = solver.getSolutionMap();
            //int[] solution = new int[data.nbClassSegments];
            //for(int i = 0; i < data.nbClassSegments; i++){
            //    String course = data.getCourseCode()[i];
            //    solution[i] = solutionMap.get(course);
            //}
            int[] solution = solver.getSolutionSlot();
            log.info("FOUND SOLUTION");
            solver.printSolution();
            for (int i = 0; i < classes.size(); i++) {
                GeneralClass gClass = classes.get(i);
                gClass.getTimeSlots().forEach(rr -> rr.setGeneralClass(null));
                gClass.getTimeSlots().clear();
            }

            for (int i = 0; i < data.nbClassSegments; i++) {
                int day = solution[i] / (Constant.slotPerCrew*2);//12;
                int t1 = solution[i] - day * Constant.slotPerCrew*2;//12;
                int K = t1 / Constant.slotPerCrew;//6; // kip
                int tietBD = t1 - Constant.slotPerCrew * K;
                //log.info("autoScheduleTimeSlotRoom, slot solution[" + i + "] = " + solution[i] + ", day = " + day + ", t1 = " + t1 + " kip = " + K + ", tietDB = " + tietBD);


                //GeneralClass gClass = classes.get(scheduleMap.get(i));

                GeneralClass gClass = D.getMClassSegment2Class().get(i);

                RoomReservation newRoomReservation = new RoomReservation(gClass.getCrew(), tietBD, tietBD + data.nbSlots[i] - 1, day + 2, null);
                newRoomReservation.setDuration(data.getNbSlots()[i]);
                newRoomReservation.setGeneralClass(gClass);
                gClass.getTimeSlots().add(newRoomReservation);

                int idxRoom = solver.getSolutionRoom()[i];
                newRoomReservation.setRoom(rooms.get(idxRoom).getClassroom());

                //log.info("class[" + i + "] is assigned to slot " + solution[i] + "(" + day + "," + K + "," + tietBD + "), room = " + idxRoom + " - " + newRoomReservation.getRoom());
            }
            //roomReservations.forEach(rr -> {
            //    rr.setRoom(rooms.get(solver.getSolution()[roomReservations.indexOf(rr)]).getClassroom());
            //});
        }
        return classes;
    }
    public static List<GeneralClass> autoScheduleTimeSlot(List<GeneralClass> classes, int timeLimit) {
        int n = classes.size();
        if (n == 0) {
            log.info("Chưa chọn lớp!");
            return null;
        }
        classes.sort(Comparator.comparing(GeneralClass::hasNonNullTimeSlot).reversed());
        for(int i = 0; i < classes.size(); i++){
            GeneralClass c = classes.get(i);
            log.info("autoScheduleTimeSlot, class[" + i + "] = " + c.toString() + " total duration" + MassExtractor.extract(c.getMass()));
        }
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
                log.info("autoScheduleTimeSlot, class " + c.getClassCode() + " has " + c.getTimeSlots().size());

                for (RoomReservation rr: c.getTimeSlots()) {
                    log.info("autoScheduleTimeSlot, roomreservation " + rr.toString());
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

        for(int i = 0; i < splitDurations.length; i++){
            log.info("autoScheduleTimeSlot, class-segment " + i + " duration = " + splitDurations[i] + " domain = " + domains[i].toArray());
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


    public static void main(String[] args){
        try{
            Gson gson = new Gson();
            MapDataScheduleTimeSlotRoomOneGroup data = new MapDataScheduleTimeSlotRoomOneGroup();
            Scanner in = new Scanner("timetable.json");
            String json = in.nextLine();
            data = gson.fromJson(json, MapDataScheduleTimeSlotRoomOneGroup.class);

            in.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}