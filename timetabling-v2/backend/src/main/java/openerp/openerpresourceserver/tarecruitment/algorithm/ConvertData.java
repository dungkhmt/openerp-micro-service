package openerp.openerpresourceserver.tarecruitment.algorithm;

import com.nimbusds.jose.util.Pair;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.tarecruitment.entity.Application;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Log4j2
public class ConvertData {
    List<String> userApplies;
    List<Integer> classCalls;
    List<Application> applications;

    List<Application> result = new ArrayList<>();;
    int V;
    Object[] memoryIndex; // "source", [userId], [classId], "sink"
    int [][]graph;

    Pair<String, Integer>[] userPairClass;

    int userSize;
    int classSize;

    public ConvertData(List<String> userApplies, List<Integer> classCalls, List<Application> applications) {
        this.userApplies = userApplies;
        this.classCalls = classCalls;
        this.applications = applications;

        userSize = userApplies.size();
        log.info("userSize has " + userSize);
        classSize = classCalls.size();
        log.info("classSize has " + classSize);

        V = userSize + classSize + 2;

        graph = new int[V][V];
        memoryIndex = new Object[V];

        // Map data with index
        memoryIndex[0] = "source";
        memoryIndex[V - 1] = "sink";

        for(int i = 1; i <= userSize; i++) {
            memoryIndex[i] = userApplies.get(i - 1);
        }

        for(int i = userSize + 1; i <= userSize + classSize; i++) {
            memoryIndex[i] = classCalls.get(i - userSize - 1);
        }

        for(int i = 0; i < V; i++) {
            log.info("Index " + i + " has value " + memoryIndex[i]);
        }

    }

    public int[][] convertDataIntoArray() {
        // Base all is 0
        for(int i = 0; i < V; i++) {
            for(int j = 0; j < V; j++) {
                graph[i][j] = 0;
            }
        }

        // Source connect to all user
        for(int i = 1; i <= userSize; i++) {
            graph[0][i] = 1;
        }

        // All class connect to sink
        for(int i = 1 + userSize; i <= V - 2; i++) {
            graph[i][V - 1] = 1;
        }

        // Set 1 to all application
        for(Application app : applications) {
            String userId = app.getUser().getId();
            int classId = app.getClassCall().getId();

            // Find index in memoryIndex
            int userIndex = 0;
            for(int i = 1; i <= userSize; i++) {
                if(memoryIndex[i].equals(userId)) {
                    userIndex = i;
                    break;
                }
            }
            log.info("User index: " + userIndex);
            if(userIndex == 0) throw new IllegalArgumentException("Wrong data");

            int classIndex = 0;
            for(int i = userSize + 1; i < V - 1; i++) {
                if(memoryIndex[i].equals(classId)) {
                    classIndex = i;
                    break;
                }
            }
            log.info("Class index: " + classIndex);
            if(classIndex == 0) throw new IllegalArgumentException("Wrong data");

            // set value 1
            graph[userIndex][classIndex] = 1;
        }
        return graph;
    }

    public List<Application> convertPairIntoApplicationList(Pair<Integer, Integer>[] userIndexToClassIndex, int numberOfPair) {
        userPairClass = new Pair[numberOfPair];
        for(int i = 0; i < numberOfPair; i++) {
            int userIndex = userIndexToClassIndex[i].getLeft();
            String userId = memoryIndex[userIndex].toString();
            log.info("User ID when check pair: " + userId);
            int classIndex = userIndexToClassIndex[i].getRight();
            int classId = (int) memoryIndex[classIndex];
            log.info("Class ID when check pair: " + classId);

            for(Application app : applications) {
                if(app.getClassCall().getId() == classId && Objects.equals(app.getUser().getId(), userId)) {
                    log.info("Application will be approved is id " + app.getId() + " of user " + app.getUser().getId()
                            + " in classId " + app.getClassCall().getId());
                    result.add(app);
                }
            }

        }
        return result;
    }
}
