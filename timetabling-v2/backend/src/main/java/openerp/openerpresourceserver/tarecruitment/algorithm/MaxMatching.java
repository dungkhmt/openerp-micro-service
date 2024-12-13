package openerp.openerpresourceserver.tarecruitment.algorithm;

import com.nimbusds.jose.util.Pair;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.tarecruitment.entity.Application;

import java.util.List;

@Log4j2
public class MaxMatching {

    List<Application> applications;
    List<String> userApplies;
    List<Integer> classCalls;

    List<Application> resultApplications;

    Pair[] indexSet;

    int V;

    int maxFlowResult;

    int[][] graph;
    private ConvertData convertData;

    public MaxMatching(List<Application> applications, List<String> userApplies, List<Integer> classCalls) {
        this.applications = applications;
        this.userApplies = userApplies;
        this.classCalls = classCalls;
        this.V = userApplies.size() + classCalls.size() + 2;

        indexSet = new Pair[V];

        convertData = new ConvertData(userApplies, classCalls, applications);
        graph = convertData.convertDataIntoArray();
        matchingClass(0, V - 1);
        resultApplications = convertData.convertPairIntoApplicationList(indexSet, maxFlowResult); // ALL APPLICATION THAT WILL CHANGE STATUS TO ACCEPTED
    }

    /**
     *
     * @param rGraph
     * @param u: index of source
     * @param t: index of sink
     * @param visited
     * @param parent: store the path
     * @return
     */
    private boolean DFS(int[][] rGraph, int u, int t, boolean[] visited, int[] parent) {
        if (u == t) {
            return true;
        }
        visited[u] = true;
        for (int v = 0; v < V; v++) {
            if (!visited[v] && rGraph[u][v] > 0) {
                parent[v] = u;
                if (DFS(rGraph, v, t, visited, parent)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     *
     * @param s: index of source
     * @param t: index of sink
     * @return
     */
    private int matchingClass(int s, int t) {
        int[][] rGraph = new int[V][V];

        // Copy graph into rGraph
        for(int i = 0; i < V; i++) {
            for(int j = 0; j < V; j++) {
                rGraph[i][j] = graph[i][j];
            }
        }

        int[] parent = new int[V];
        boolean[] visited = new boolean[V];
        int maxFlow = 0;

        // If there is path
        while(DFS(rGraph, s, t, visited, parent)) {
            int pathFlow = Integer.MAX_VALUE;

            // WILL UPDATE THIS SINCE PATH FLOW'S VALUE IS ONLY 1
            // Update pathFlow
            for(int v = t; v != s; v = parent[v]) {
                int u = parent[v];
                pathFlow = Math.min(pathFlow, rGraph[u][v]);
            }

            // Update rGraph
            for(int v = t; v != s; v = parent[v]) {
                int u = parent[v];
                rGraph[u][v] -= pathFlow;
                rGraph[v][u] += pathFlow;
            }

            int classIndex = parent[t];
            int userIndex = parent[classIndex];

            indexSet[maxFlow] = Pair.of(userIndex, classIndex);

            maxFlow += pathFlow;

            // Reset visited array for the next iteration
            visited[s] = false;
            visited[t] = false;

        }
        log.info("Max flow: " + maxFlow);
        for(int i = 0; i < maxFlow; i++) {
            log.info("User at index " + indexSet[i].getLeft() + " can assign to class at index " + indexSet[i].getRight());
        }
        maxFlowResult = maxFlow;
        return maxFlow;
    }

    public int[][] getGraph() {
        return graph;
    }

    public List<Application> getAssignApplications() {
        return resultApplications;
    }

}
