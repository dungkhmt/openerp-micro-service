package openerp.openerpresourceserver.tarecruitment.algorithm;

import com.nimbusds.jose.util.Pair;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.tarecruitment.entity.Application;
import openerp.openerpresourceserver.tarecruitment.entity.ClassCall;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Log4j2
public class ConvertDataV2 {
    List<Application> applicationList;
    List<String> userIdList;
    List<ClassCall> classCallList;
    List<Application> result = new ArrayList<>();

    String[] userIndex;

    int[] classIdIndex;

    int [][] request;

    int [][] conflict;

    int[] durations; // number of session slots

    int userSize;
    int classSize;

    private OrToolsTaRecruitment orToolsTaRecruitment;

    public ConvertDataV2(List<Application> applicationList, List<String> userIdList, List<ClassCall> classCallList) {
        this.applicationList = applicationList;
        this.userIdList = userIdList;
        this.classCallList = classCallList;

        userSize = userIdList.size();
        classSize = classCallList.size();
        durations = new int[classSize];


        // user pair
        userIndex = new String[userSize];
        for(int i = 0; i < userSize; i++) {
            userIndex[i] = userIdList.get(i);
        }

        // class pair
        classIdIndex = new int[classSize];
        for(int i = 0; i < classSize; i++) {
            classIdIndex[i] = classCallList.get(i).getId();
            durations[i] = classCallList.get(i).getNumberSlots();
        }

        // set up request array
        request = new int[userSize][classSize];
        for(int i = 0; i < userSize; i++) {
            for(int j = 0; j < classSize; j++) {
                request[i][j] = 0;
            }
        }

        // request = 1 if there is application for that pair
        for(Application application : applicationList) {

            String userId = application.getUser().getId();
            int userPairIndex = 0;
            for(int i = 0; i < userSize; i++) {
                if(Objects.equals(userIndex[i], userId)) {
                    userPairIndex = i;
                    break;
                }
            }

            int classId = application.getClassCall().getId();
            int classPairIndex = 0;
            for(int i = 0; i < classSize; i++) {
                if(classIdIndex[i] == classId) {
                    classPairIndex = i;
                    break;
                }
            }

            request[userPairIndex][classPairIndex] = 1;
        }

        // set up conflict array
        conflict = new int[classSize][classSize];
        for(int i = 0; i < classSize; i++) {
            for(int j = 0; j < classSize; j++) {
                conflict[i][j] = 0;
            }
        }

        // check if there is conflict, set into 1
        for(int i = 0; i < classSize; i++) {
            for(int j = i + 1; j < classSize; j++) {
                int class1Day = classCallList.get(i).getDay();
                int class2Day = classCallList.get(j).getDay();
                if(class1Day != class2Day) continue;
                else {
                    int class1StartPeriod = classCallList.get(i).getStartPeriod();
                    int class1EndPeriod = classCallList.get(j).getEndPeriod();
                    int class2StartPeriod = classCallList.get(i).getStartPeriod();
                    int class2EndPeriod = classCallList.get(j).getEndPeriod();

                    //if (class1StartPeriod < class2EndPeriod && class1EndPeriod > class2StartPeriod) {
                    //    conflict[i][j] = 1;
                    //    conflict[j][i] = 1;
                    //}
                    boolean disjoint = class1EndPeriod < class2StartPeriod
                            || class2EndPeriod < class1StartPeriod;
                    if(!disjoint){
                        conflict[i][j] = 1; conflict[j][i] = 1;
                    }
                }
            }
        }

        orToolsTaRecruitment = new OrToolsTaRecruitment(classSize, userSize, request, conflict, durations);

    }

    public List<Application> solvingProblem() {

        int[][] resultIndex;
        //resultIndex = orToolsTaRecruitment.solving();
        resultIndex = orToolsTaRecruitment.newSolve();


        log.info("REQUEST ARRAY");
        for(int i = 0; i < userSize; i++) {
            for(int j = 0; j < classSize; j++) {
                log.info("resultIndex[" + i + "][" + j + "] = " + resultIndex[i][j]);
                if(resultIndex[i][j] == 1) {
                    log.info("Approve user " + userIndex[i] + " class " + classIdIndex[j]);
                }
            }
        }

        for(int i = 0; i < userSize; i++) {
            for(int j = 0; j < classSize; j++) {
                if(resultIndex[i][j] == 1) {
                    String user = userIndex[i];
                    int classId = classIdIndex[j];

                    for(Application application : applicationList) {
                        if(Objects.equals(application.getUser().getId(), user)
                                && application.getClassCall().getId() == classId) {
                            result.add(application);
                        }
                    }
                }
            }
        }

        return result;
    }

}
