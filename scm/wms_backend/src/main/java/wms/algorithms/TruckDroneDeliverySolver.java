package wms.algorithms;

import wms.algorithms.entity.*;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Setter
@Getter
public class TruckDroneDeliverySolver {
    private TruckDroneDeliveryInput constructedInput;
    private List<Node> tspBootstrappingSolution;
    private List<List<Node>> globalTruckSubRoutes;
    private List<Node> globalTruckRoute;
    private List<Node> globalCustomers;
    private int bestLaunchIndex;
    private int bestVisitIndex;
    private int bestRendezvousIndex;
    private int bestSubrouteIndex;
    private double maxSavings;
    private boolean isDroneNode;
    private boolean isStop;
    private TruckDroneDeliverySolutionOutput solution;
    public TruckDroneDeliverySolver(TruckDroneDeliveryInput input) {
        this.constructedInput = input;
        // First, calculate TSP solution for tour with only truck nodes
        // This is a bootstrapping method for TSP-LS solution.
        NearestNeighborSolver solver = new NearestNeighborSolver(constructedInput.getLocations(), constructedInput.getDistances());
        solver.calculateTSPTour(0);
        // Second. Assign global variables with value in order to reuse them later.
        this.tspBootstrappingSolution = solver.getTSPSolution();
        this.globalTruckRoute = new ArrayList<>(this.tspBootstrappingSolution);
        // https://stackoverflow.com/questions/68786072/concurrentmodificationexception-while-making-removeall-method
        this.globalCustomers = new ArrayList<>(this.tspBootstrappingSolution.subList(1, tspBootstrappingSolution.size()-1));
        List<List<Node>> truckSubRoutes = new ArrayList<>();
        truckSubRoutes.add(this.globalTruckRoute);
        this.globalTruckSubRoutes = truckSubRoutes;
        List<DroneRoute> droneRoutes = new ArrayList<>();
        TruckRoute truckRoute = new TruckRoute(this.globalTruckRoute);
        this.solution = new TruckDroneDeliverySolutionOutput(constructedInput, truckRoute, droneRoutes);
        this.isStop = false;
        this.bestLaunchIndex = -1;
        this.bestVisitIndex = -1;
        this.bestRendezvousIndex = -1;
        this.bestSubrouteIndex = -1;
        this.maxSavings = 0.0;
    }
    public TruckDroneDeliverySolutionOutput solve(){
        int maximumIterate = 10; // When there is no improvement, terminate after 10 iterations
        while (!isStop) {
            if (maximumIterate < 0) this.isStop = true;
            System.out.println("Starting new loop ==============================================");
            for (int j = 0; j < globalCustomers.size(); j++) {
//                System.out.println("Checking customer: " + globalCustomers.get(j).getName());
                // Algorithm 6: Calculate savings;
                double savings = calcSaving(j);
                for (int i = 0; i < globalTruckSubRoutes.size(); i++) {
                    List<Node> truckSubRoute = globalTruckSubRoutes.get(i);
                    // When checking node is one of elements in this SubRoute, no need to relocate itseft into this route.
                    if (isSubRouteAssociateWithDroneDelivery(truckSubRoute, solution)) {
                        boolean shouldRelocate = true;
                        for (Node customer: truckSubRoute) {
                            if (customer.getName().equals(globalCustomers.get(j).getName())) {
                                shouldRelocate = false;
                                break;
                            }
                        }
                        if (shouldRelocate) {
                            // Algo 7: Relocate a node as a truck node
                            relocateAsTruck(j, i, truckSubRoute, savings);
                        }
                    } else {
                        // Algo 8: Relocate a node as a drone node
                        relocateAsDrone(j, i, truckSubRoute, savings);
                    }
                }
            }
            if (this.maxSavings > 0) {
                // Algo 9: Apply changes (new solution) if having better solution.
                applyChanges();
                this.maxSavings = 0;
            }
            else {
                this.isStop = true;
            }
            maximumIterate-=1;
        }

        // TODO: assign value for droneRoutes and truckRoutes
        TruckRoute truckRoute = new TruckRoute(this.globalTruckRoute);
        solution.setTruckRoute(truckRoute);
        double globalTSPCost = 0.0;
        for (int i = 0; i < this.tspBootstrappingSolution.size() - 1; i++) {
            globalTSPCost += constructedInput.getDistanceBtwTwoNodesByNode(this.tspBootstrappingSolution.get(i),
                    this.tspBootstrappingSolution.get(i+1)) * constructedInput.getTruck().getTransportCostPerUnit();
            ;
        }
        solution.setTotalTSPCost(globalTSPCost);
        System.out.println("TSP cost: " + globalTSPCost);
        System.out.println("TSP-D cost: " + solution.calculateTotalCost());
        return solution;
    }
    public double calcSaving(int customerIndex) {
        Node currentCustomer = this.globalCustomers.get(customerIndex);
        Node prevCustomer = getPreviousRouteSequenceNode(currentCustomer, this.globalTruckRoute);
        Node nextCustomer = getNextRouteSequenceNode(currentCustomer, this.globalTruckRoute);
        double dij = constructedInput.getDistanceBtwTwoNodesByNode(prevCustomer, currentCustomer);
        double djk = constructedInput.getDistanceBtwTwoNodesByNode(currentCustomer, nextCustomer);
        double dik = constructedInput.getDistanceBtwTwoNodesByNode(prevCustomer, nextCustomer);
        double savings = (dij + djk - dik) * constructedInput.getTruck().getTransportCostPerUnit();
        // TODO: Add case associated with a drone for a specific candidated customer. => Already did.
        /**
         * For any i, k belongs to truckRoute, <i, j, k > => isPossibleDroneDelivery = True.
         * Read more of why this is brought into consideration in this paper: The Flying sidekick TSP.
         */
        List<Node> subRouteWithDD = getSubRouteAssociateWithDroneDelivery(customerIndex);
        if ( subRouteWithDD != null) {
            Node first = subRouteWithDD.get(0);
            Node last = subRouteWithDD.get(subRouteWithDD.size() - 1);
            Node customerNode = globalCustomers.get(customerIndex);
            List<Node> droneRoute = new ArrayList<>();
            droneRoute.add(first);
            droneRoute.add(customerNode);
            droneRoute.add(last);
            double timeCostIJ = constructedInput.getTravellingTimeBtwTwoNodes(first, customerNode);
            double timeCostJK = constructedInput.getTravellingTimeBtwTwoNodes(customerNode, last);
            double timeCostIK = constructedInput.getTravellingTimeBtwTwoNodes(first, last);
            double timeCostTruckTourIK = constructedInput.calTruckTimeTravellingInTour(subRouteWithDD);
            double timeCostDrone = constructedInput.calDroneTimeFlyingInTour(droneRoute);
            double totalTimeSavingIfRemoved = timeCostTruckTourIK - timeCostIJ - timeCostJK + timeCostIK - timeCostDrone;
            double truckingWaitingCostSaving = constructedInput.getTruck().getWaitingCost() *
                    Math.max(0, totalTimeSavingIfRemoved);
            double droneWaitingCostSaving = constructedInput.getDrone().getWaitingCost() *
                    Math.max(0, -totalTimeSavingIfRemoved);
            savings = savings + truckingWaitingCostSaving + droneWaitingCostSaving;
        }
//        System.out.println("Savings : " + savings);
        return savings;
    }
    private Node getPreviousRouteSequenceNode(int index, List<Node> truckRoute) {
        if (index == 0) return null;
        return truckRoute.get(index - 1);
    }
    private Node getPreviousRouteSequenceNode(Node node, List<Node> truckRoute) {
        for (int i = 1; i < truckRoute.size(); i++) {
            if (node.getName().equals(truckRoute.get(i).getName())) {
                return truckRoute.get(i-1);
            }
        }
        return null;
    }
    private Node getNextRouteSequenceNode(int index, List<Node> truckRoute) {
        if (truckRoute.size() == index)
            return null;
        return truckRoute.get(index + 1);
    }
    private Node getNextRouteSequenceNode(Node node, List<Node> truckRoute) {
        for (int i = 0; i < truckRoute.size() - 1; i++) {
            if (node.getName().equals(truckRoute.get(i).getName())) {
                return truckRoute.get(i+1);
            }
        }
        return null;
    }
    private boolean isSubRouteAssociateWithDroneDelivery(List<Node> truckSubRoute, TruckDroneDeliverySolutionOutput sol) {
        List<DroneRoute> droneRoutes = sol.getDroneRoutes();
        for (DroneRoute droneRoute: droneRoutes) {
            if (isTruckRoutePairWithADrone(truckSubRoute, droneRoute)) return true;
        }
        return false;
    }
    private List<Node> getSubRouteAssociateWithDroneDelivery(int candidate) {
        /**
         * To be associated, a candidate must be contained in a truck subRoute
         * that has first node and last node the same as lauch node and rendezvous node of a drone route.
         */
        List<DroneRoute> droneRoutes = solution.getDroneRoutes();
        List<Node> candidateBelongedSubRoute = getCandidateSubRoute(candidate);
        if (candidateBelongedSubRoute == null) return null;
        for (DroneRoute droneRoute: droneRoutes) {
            if (isTruckRoutePairWithADrone(candidateBelongedSubRoute, droneRoute)) return candidateBelongedSubRoute;
        }
        return null;
    }
    private List<Node> getCandidateSubRoute(int candidate) {
        Node candidateNode = this.globalCustomers.get(candidate);
        for (List<Node> subRoute: globalTruckSubRoutes) {
            for (Node node : subRoute) {
                if (node.getName().equals(candidateNode.getName())) {
                    return subRoute;
                }
            }
        }
        return null;
    }
    private boolean isTruckRoutePairWithADrone(List<Node> truckRoute, DroneRoute droneRoute) {
        Node firstTruckEle = truckRoute.get(0);
        Node lastTruckEle = truckRoute.get(truckRoute.size() - 1);
        DroneRouteElement launchEle = droneRoute.getDroneRouteElements().get(0);
        DroneRouteElement rendezvousEle = droneRoute.getDroneRouteElements().get(2);
        return firstTruckEle.getName().equals(launchEle.getLocationID()) &&
                lastTruckEle.getName().equals(rendezvousEle.getLocationID());
    }
    private boolean isPossibleDroneDelivery(List<Node> droneRoute) {
        if (droneRoute.size() > 3) {
            return false; // drone delivery should contain no more than 3 nodes
        }
        Node first = droneRoute.get(0);
        Node middle = droneRoute.get(1);
        Node last = droneRoute.get(2);

        if (first.getName().equals(middle.getName()) || first.getName().equals(last.getName()) || middle.getName().equals(last.getName())) {
            return false;
        }
        double enduranceTime = constructedInput.getDrone().getDurationCapacity();
        double visitTime = constructedInput.getFlyingTimeBtwTwoNodes(first, middle);
        double rendezvousTime = constructedInput.getFlyingTimeBtwTwoNodes(middle, last);
        if (visitTime + rendezvousTime > enduranceTime) {
            System.out.println("Drone for node " + first.getName() + "||" + middle.getName() + "||" + last.getName() + " is not in endurance limit");
        }
        return visitTime + rendezvousTime <= enduranceTime;
    }
    /**
     * Algorithm 7: Calculates the cost of relocating the customer j
     * into a different position in the truck's route.
     */
    private void relocateAsTruck(int customerIndex, int subRouteIndex, List<Node> subRoute, double currSavings) {
        Node customer = this.globalCustomers.get(customerIndex);
        for (int i = 0 ; i < subRoute.size() - 1; i++) {
            // TODO: Enhancing this distance calculating by using distanceMatrix.
            double distanceIJ = constructedInput.getDistanceBtwTwoNodesByNode(subRoute.get(i), customer);
            double distanceJK = constructedInput.getDistanceBtwTwoNodesByNode(customer, subRoute.get(i+1));
            double distanceIK = constructedInput.getDistanceBtwTwoNodesByNode(subRoute.get(i), subRoute.get(i+1));
            double delta = (distanceIJ + distanceJK - distanceIK) * constructedInput.getTruck().getTransportCostPerUnit();
            double timeTravel = distanceIJ / constructedInput.getTruck().getSpeed() +
                    distanceJK / constructedInput.getTruck().getSpeed();
            if (delta < currSavings) {
                // Why delta < savings? Cos if not, there is no point in relocating a truck node to another position
                // that increases the total cost and also that will make currSavings - delta negative.
                if (constructedInput.getDrone().isAbleToFly(timeTravel)) { // TODO: Calculate this addedUpCost.
                    // Why check this ableToFly conditions?
                    // Cos we are adding new node -> cost would likely to increase the time travel of the added subroute.
                    if (currSavings - delta > maxSavings) {
                        isDroneNode = false;
                        bestVisitIndex = getIndexOfNodeInTSPSol(customer);
                        bestLaunchIndex = getIndexOfNodeInTSPSol(subRoute.get(i));
                        bestRendezvousIndex = getIndexOfNodeInTSPSol(subRoute.get(i+1));
                        maxSavings = currSavings - delta;
                        bestSubrouteIndex = subRouteIndex;
//                        System.out.println("Relocating as truck: " + bestLaunchIndex + " " + bestVisitIndex + " " + bestRendezvousIndex);
//                        System.out.println("Max savings truck - subrouteInd " + maxSavings + "-" + bestSubrouteIndex);

                        //TODO: Should state the subroute this best solution belongs to. => Already. By saving bestSubrouteIndex
                    }
                }
            }
        }
    }

    /**
     * Algorithm 8: Calculates the cost of relocating customer j
     * as a drone node
     */
    private void relocateAsDrone(int customerIndex, int subRouteIndex, List<Node> subRoute, double currSavings) {
        for (int i = 0; i < subRoute.size() - 1; i++) {
            for (int k = i + 1; k < subRoute.size(); k++) {
                List<Node> checkingDroneRoute = new ArrayList<>();
                Node first = subRoute.get(i);
                Node last = subRoute.get(k);
                Node customer = globalCustomers.get(customerIndex);
                checkingDroneRoute.add(first);
                checkingDroneRoute.add(customer);
                checkingDroneRoute.add(last);
                if (isPossibleDroneDelivery(checkingDroneRoute)) {
                    double truckTime = constructedInput.calTruckTimeTravellingInTour(subRoute.subList(i, k+1));
                    double droneTime = constructedInput.calDroneTimeFlyingInTour(checkingDroneRoute);
                    double waitingTime = Math.abs(truckTime - droneTime);
                    double waitingCost = truckTime > droneTime ? // drone have to wait ? if true -> cost on drone.
                            constructedInput.getDrone().getWaitingCost() * waitingTime : constructedInput.getTruck().getWaitingCost() * waitingTime;
                    double distanceIJ = constructedInput.getDistanceBtwTwoNodesByNode(first, customer);
                    double distanceJK = constructedInput.getDistanceBtwTwoNodesByNode(customer, last);
                    double delta = (distanceIJ + distanceJK) * constructedInput.getDrone().getTransportCostPerUnit()
                            + waitingCost;

                    if (currSavings - delta > maxSavings) {
//                        System.out.println("Prev maxSavings: " + maxSavings);
                        isDroneNode = true;
                        bestVisitIndex = getIndexOfNodeInTSPSol(customer);
                        bestLaunchIndex = getIndexOfNodeInTSPSol(first);
                        bestRendezvousIndex = getIndexOfNodeInTSPSol(last);
                        maxSavings = currSavings - delta;
                        bestSubrouteIndex = subRouteIndex;
//                        System.out.println("Curr savings - delta " + currSavings + "-" + delta);
//                        System.out.println("Relocating as drone: " + bestLaunchIndex + " " + bestVisitIndex + " " + bestRendezvousIndex);
//                        System.out.println("Max savings drone - subrouteInd " + maxSavings + "-" + bestSubrouteIndex);
                        //TODO: Should state the subroute this best solution belongs to. => Already, same as algo 7
                    }
                }
            }
        }
    }
    // Algorithm 9: Update solution when there is a better one
    private void applyChanges() {
        if (isDroneNode) {
            // Step 1: Assign new drone route
            List<Node> droneRouteNode = new ArrayList<>();
            Node launchNode = this.tspBootstrappingSolution.get(bestLaunchIndex);
            Node visitNode = this.tspBootstrappingSolution.get(bestVisitIndex);
            Node rendezvousNode = this.tspBootstrappingSolution.get(bestRendezvousIndex);
            droneRouteNode.add(launchNode);
            droneRouteNode.add(visitNode);
            droneRouteNode.add(rendezvousNode);
            DroneRoute droneRoute = new DroneRoute(droneRouteNode);
            solution.getDroneRoutes().add(droneRoute);
            // Step 2: Removing j*
            int subRouteForRemovingIndex = findSubRouteContainNodeFromSubRoutes(visitNode, globalTruckSubRoutes);
            List<Node> subRouteBeforeRemoving = new ArrayList<>(globalTruckSubRoutes.get(subRouteForRemovingIndex));
            RemoveNodeReturnValue subRouteAfterRemoving = removeNodeFromRoute(visitNode, subRouteBeforeRemoving); // remove from local subRoutes
            globalTruckSubRoutes.set(subRouteForRemovingIndex, subRouteAfterRemoving.route);
            this.globalTruckRoute = removeNodeFromRoute(visitNode, globalTruckRoute).route; // Remove from global truck route
            // Append a new truck subRoute
            // For i is launchIndex, k is rendezvousIndex
            // => [0, i] ; [i, k]; [k, end) is subroute after removing j and append new route with drone delivery
            List<Node> bestSubRoute = this.globalTruckSubRoutes.get(bestSubrouteIndex);
            int leftBoundIndex = getIndexOfNodeInSubRoute(launchNode, bestSubRoute);
            int rightBoundIndex = getIndexOfNodeInSubRoute(rendezvousNode, bestSubRoute);
            List<Node> droneSubRoute = bestSubRoute.subList(leftBoundIndex, rightBoundIndex + 1);
            if (leftBoundIndex > 0) {
                globalTruckSubRoutes.add(bestSubRoute.subList(0, leftBoundIndex+1));
            }
            if (rightBoundIndex < bestSubRoute.size() - 1) {
                globalTruckSubRoutes.add(bestSubRoute.subList(rightBoundIndex, bestSubRoute.size()));
            }
            globalTruckSubRoutes.set(bestSubrouteIndex, droneSubRoute); // Best route will be replaced by new drone route
            // Step 3: Removing i*, j*, k* from customers
            globalCustomers.remove(this.tspBootstrappingSolution.get(bestLaunchIndex));
            globalCustomers.remove(this.tspBootstrappingSolution.get(bestVisitIndex));
            globalCustomers.remove(this.tspBootstrappingSolution.get(bestRendezvousIndex));
        }
        else {
            Node nodeToProceed = tspBootstrappingSolution.get(bestVisitIndex);
            // Step 1: Removing from current subRoute
            int subRouteForRemovingIndex = findSubRouteContainNodeFromSubRoutes(nodeToProceed, globalTruckSubRoutes);
            List<Node> subRouteBeforeRemoving = new ArrayList<>(globalTruckSubRoutes.get(subRouteForRemovingIndex));
            RemoveNodeReturnValue subRouteAfterRemoving = removeNodeFromRoute(nodeToProceed, subRouteBeforeRemoving);
            globalTruckSubRoutes.set(subRouteForRemovingIndex, subRouteAfterRemoving.route);
            // Step 2: Inserting to another subRoute
            List<Node> truckSubRouteToInsert = globalTruckSubRoutes.get(bestSubrouteIndex);
            List<Node> truckSubRouteAfterInserting = insertNodeToRoute(nodeToProceed, tspBootstrappingSolution.get(bestLaunchIndex), truckSubRouteToInsert);
            globalTruckSubRoutes.set(bestSubrouteIndex, truckSubRouteAfterInserting);
        }
    }
    static class RemoveNodeReturnValue {
        int removingIndex;
        List<Node> route;

    }
    private RemoveNodeReturnValue removeNodeFromRoute(Node removingNode, List<Node> subRoute) {
        RemoveNodeReturnValue results = new RemoveNodeReturnValue();
        List<Node> removedSubRoute = new ArrayList<>();
        for (int i = 0; i < subRoute.size(); i++) {
            if (subRoute.get(i).getName().equals(removingNode.getName())) {
                results.removingIndex = i;
                continue;
            }
            removedSubRoute.add(subRoute.get(i));
        }
        results.route = new ArrayList<>(removedSubRoute);
        return results;
    }
    private List<Node> insertNodeToRoute(Node insertingNode, Node nodeBeforeInsert, List<Node> subRoute) {
//        subRoute.add(insertFromRange, insertingNode);
        List<Node> newSubRoute = new ArrayList<>();
        for (int i = 0; i < subRoute.size(); i++) {
            newSubRoute.add(subRoute.get(i));
            if (subRoute.get(i).getName().equals(nodeBeforeInsert.getName())) {
                newSubRoute.add(insertingNode);
            }
        }
        return newSubRoute;
    }
    private int findSubRouteContainNodeFromSubRoutes(Node nodeToFind, List<List<Node>> subRoutes) {
        for (int i = 0; i < subRoutes.size(); i++) {
            List<Node> subRoute = subRoutes.get(i);
            for (Node node: subRoute) {
                if (node.getName().equals(nodeToFind.getName())) {
                    return i;
                }
            }
        }
        return -1;
    }
    public TruckDroneDeliverySolutionOutput getSolution() {
        return this.solution;
    }

    public int getIndexOfNodeInTSPSol(Node node) {
        if (node.getName().contains("Vincom")) return 0;
        for (int i = 0; i < tspBootstrappingSolution.size(); i++) {
            if (node.getName().equals(tspBootstrappingSolution.get(i).getName()))
                return i;
        }
        return -1;
    }
    public int getIndexOfNodeInSubRoute(Node node, List<Node> subRoute) {
        for (int i = 0; i < subRoute.size(); i++) {
            if (node.getName().equals(subRoute.get(i).getName())) return i;
        }
        return -1;
    }
}
