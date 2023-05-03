package wms.algorithms;

import wms.algorithms.drawings.DrawingTools;
import wms.algorithms.entity.*;
import wms.algorithms.io.InputIO;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Main extends JFrame{
    public static void main(String[] args) {
        try {
            // Read coordination data from file.
            File myObj = new File("/Users/hoangbui/Desktop/openerp-micro-service/scm/wms_backend/src/main/java/wms/algorithms/data/route.txt");
            TruckDroneDeliveryInput input = InputIO.readInputFromFile(myObj);

            // Bootstraping with a TSP solution
            NearestNeighborSolver solver = new NearestNeighborSolver(input.getLocations(), input.getDistances());
            solver.calculateTSPTour(0);
            List<Node> tspSolution = solver.getTSPSolution();
            for (Node node : tspSolution) {
                log.info("Node info {} ({}, {})", node.getName(), node.getX(), node.getY());
            }
            DrawingTools drawingTools = new DrawingTools();
            drawingTools.drawSolution(tspSolution, 400, 400, "TSP Solution", false);

            // Solving TSP-LS heuristically
            TruckDroneDeliverySolver heuristicSolver = new TruckDroneDeliverySolver(input);
            heuristicSolver.solve();
            TruckDroneDeliverySolutionOutput finalSolution = heuristicSolver.getSolution();
            List<Node> truckRoute = finalSolution.convertTruckRouteToNode(finalSolution.getTruckRoute());
            List<List<Node>> droneRoutes = new ArrayList<>();
            for (DroneRoute ele: finalSolution.getDroneRoutes()) {
                List<Node> droneRoute = finalSolution.convertDroneRouteToNode(ele);
                droneRoutes.add(droneRoute);
            }
            for (Node node : truckRoute) {
                log.info("Truck node info {} ({}, {})", node.getName(), node.getX(), node.getY());
            }
            for (List<Node> drone : droneRoutes) {
                log.info("====================================");
                for (Node node : drone) {
                    log.info("Drone node info {} ({}, {})", node.getName(), node.getX(), node.getY());
                }
            }
//            DrawingTools drawingTools = new DrawingTools();
            drawingTools.drawSolution(truckRoute, droneRoutes, 400, 400, "TSP-LS", false);
        } catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static void runAlgorithm(TruckDroneDeliveryInput input) {
        TruckDroneDeliverySolver heuristicSolver = new TruckDroneDeliverySolver(input);
        heuristicSolver.solve();
    }
}
