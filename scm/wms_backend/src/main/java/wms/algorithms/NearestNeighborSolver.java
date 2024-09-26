package wms.algorithms;

import wms.algorithms.entity.DistanceElement;
import wms.algorithms.entity.Node;

import java.util.ArrayList;
import java.util.List;

public class NearestNeighborSolver {
    private final List<List<DistanceElement>> adjacencyMatrix;
    private final List<Node> tour;
    private final List<Integer> visitedNodes;
    private List<Integer> bestTour;
    private double costOfBestTour;
    private double tempCostOfCurrentTour;
    public NearestNeighborSolver(List<Node> tour, List<List<DistanceElement>> adjacencyMatrix) {
        this.tour = tour;
        this.adjacencyMatrix = adjacencyMatrix;
        this.tempCostOfCurrentTour = 0.0;
        this.visitedNodes = new ArrayList<>();
        this.bestTour = new ArrayList<>();
        this.costOfBestTour = Double.POSITIVE_INFINITY;
    }
    public void calculateTSPTour(int startNode) {
        ArrayList<Integer> tmpSolution = new ArrayList<>();
        this.visitedNodes.clear();
        this.tempCostOfCurrentTour = 0.0;
        tmpSolution.add(startNode);
        visitedNodes.add(startNode);
        int currentNode = startNode;
        while (visitedNodes.size() < tour.size()) {
            int prevNode = currentNode;
            currentNode = getNearestNode(currentNode);
            visitedNodes.add(currentNode);
            tmpSolution.add(currentNode);
            tempCostOfCurrentTour +=  findDistanceBetweenTwoNode(prevNode, currentNode);
        }
        tempCostOfCurrentTour += findDistanceBetweenTwoNode(currentNode, startNode);
        if (tempCostOfCurrentTour < costOfBestTour) {
            costOfBestTour = tempCostOfCurrentTour;
            bestTour = (List<Integer>) tmpSolution.clone();
        }
    }
    private int getNearestNode(int currentNode) {
        double edge = -1.0;
        int nearestNodeIndex = -1;
        /**
         * Search for the nearest neighbor starting from the highest
         * location/node (end-of-array), in order to always select the lowest
         * node, in the cases where two location/nodes have the same exact
         * distance. Tie-breaker strategy.
         */
        for (int i = (tour.size() - 1); i > -1; i--) {
            // already visited node
            if (isMarkedVisitedNode(i)) {
                continue;
            }
            double distance = findDistanceBetweenTwoNode(currentNode, i);
            if (distance == 0.0) {
                // same nodes, bypass
                continue;
            }
            // if check first adjacent node => assign values.
            if (edge == -1.0) {
                edge = distance;
            }
            if (distance <= edge) {
                edge = distance;
                nearestNodeIndex = i;
            }
        }
        return nearestNodeIndex;
    }
    private boolean isMarkedVisitedNode(int nodeIndex) {
        boolean found = false;
        for (int visitedNode : visitedNodes) {
            if (nodeIndex == visitedNode) {
                found = true;
                break;
            }
        }
        return found;
    }
    private double findDistanceBetweenTwoNode(int nodeAIndex, int nodeBIndex) {
        return adjacencyMatrix.get(nodeAIndex).get(nodeBIndex).getDistance();
    }

    public List<Node> getTSPSolution() {
        List<Node> tspSolution = new ArrayList<>();
        for (int nodeIndex : bestTour) {
            tspSolution.add(tour.get(nodeIndex));
        }
        tspSolution.add(tour.get(0)); // append depot as last visited address;
        return tspSolution;
    }
}
