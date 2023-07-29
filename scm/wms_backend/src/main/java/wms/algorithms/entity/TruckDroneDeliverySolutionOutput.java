package wms.algorithms.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TruckDroneDeliverySolutionOutput {
    private TruckRoute truckRoute;
    private List<DroneRoute> droneRoutes;
    private TruckDroneDeliveryInput globalInput;
    private double totalCost;
    private double totalTruckCost;
    private double totalDroneCost;
    private double totalTruckWait;
    private double totalDroneWait;
    private double totalTSPCost;
    public TruckDroneDeliverySolutionOutput(TruckDroneDeliveryInput input, TruckRoute truckRoute, List<DroneRoute> droneRoutes) {
        this.truckRoute = truckRoute;
        this.droneRoutes = droneRoutes;
        this.globalInput = input;
        this.totalCost = 0;
        this.totalTruckCost = 0;
        this.totalDroneCost = 0;
        this.totalTruckWait = 0;
        this.totalDroneWait = 0;
        this.totalTSPCost = 0;
    }
    public double calculateDroneCost() {
        double droneCost = 0.0;
        for (DroneRoute droneRoute : droneRoutes) {
            Node launchNode = droneRoute.getDroneRouteElements().get(0).getNode();
            Node visitNode = droneRoute.getDroneRouteElements().get(1).getNode();
            Node rendezvousNode = droneRoute.getDroneRouteElements().get(2).getNode();
            droneCost = (globalInput.getDistanceBtwTwoNodesByNode(launchNode, visitNode) +
                    globalInput.getDistanceBtwTwoNodesByNode(visitNode, rendezvousNode))
                    * globalInput.getDrone().getTransportCostPerUnit();
        }
        this.totalDroneCost = droneCost;
        return droneCost;
    }
    public double calculateTruckCost() {
        double truckCost = 0.0;
        for (int i = 0; i < truckRoute.getRouteElements().size() - 1; i++) {
            Node currNode = truckRoute.getRouteElements().get(i).getNode();
            Node nextNode = truckRoute.getRouteElements().get(i+1).getNode();
            truckCost += globalInput.getDistanceBtwTwoNodesByNode(currNode, nextNode) * globalInput.getTruck().getTransportCostPerUnit();
        }
        this.totalTruckCost = truckCost;
        return truckCost;
    }
    public double calculateWaitingCost() {
        double waitingCost = 0.0;
        for (DroneRoute droneRoute : droneRoutes) {
            List<Node> droneRouteNode = droneRoute.convertToListNode(droneRoute.getDroneRouteElements());
            List<Node> associatedTruckNode = getSubRouteAssociateWithDroneRoute(droneRouteNode);
            double truckTime = globalInput.calTruckTimeTravellingInTour(associatedTruckNode);
            double droneTime = globalInput.calDroneTimeFlyingInTour(droneRouteNode);
            double waitingTime = Math.abs(truckTime - droneTime);
            waitingCost += truckTime > droneTime ? // drone have to wait ? if true -> cost on drone.
                    globalInput.getDrone().getWaitingCost() * waitingTime : globalInput.getTruck().getWaitingCost() * waitingTime;
        }
        this.totalTruckWait = waitingCost;
        this.totalDroneWait = waitingCost;
        return waitingCost;
    }

    public double calculateTotalCost() {
        double waitingCost = this.calculateWaitingCost();
        double truckCost = this.calculateTruckCost();
        double droneCost = this.calculateDroneCost();
        this.totalCost = waitingCost + truckCost + droneCost;
        return this.totalCost;
    }
    public List<Node> convertTruckRouteToNode(TruckRoute truckRoute) {
        List<Node> truckNode = new ArrayList<>();
        for (TruckRouteElement ele : truckRoute.getRouteElements()) {
            truckNode.add(ele.getNode());
        }
        return truckNode;
    }
    public List<Node> convertDroneRouteToNode(DroneRoute droneRoute) {
        List<Node> droneNode = new ArrayList<>();
        for (DroneRouteElement ele : droneRoute.getDroneRouteElements()) {
            droneNode.add(ele.getNode());
        }
        return droneNode;
    }

    public List<Node> getSubRouteAssociateWithDroneRoute(List<Node> droneRoute) {
        Node launchNode = droneRoute.get(0);
        Node rendezvousNode = droneRoute.get(2);
        List<Node> originTruckRoute = this.truckRoute.convertToListNode(this.truckRoute.getRouteElements());
        int leftBound = 0;
        int rightBound = this.truckRoute.getRouteElements().size();
        for (int i = 0; i < rightBound; i++) {
            Node currNode = this.truckRoute.getRouteElements().get(i).getNode();
            if (currNode.getName().equals(launchNode.getName())) {
                leftBound = i;
            }
            if (currNode.getName().equals(rendezvousNode.getName())) {
                rightBound = i+1;
            }
        }
        return originTruckRoute.subList(leftBound, rightBound);
    }
}
