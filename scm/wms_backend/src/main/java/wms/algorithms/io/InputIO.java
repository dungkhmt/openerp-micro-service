package wms.algorithms.io;

import wms.algorithms.entity.*;
import wms.algorithms.utils.Utils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class InputIO {
    public static TruckDroneDeliveryInput readInputFromFile(String filePath) throws IOException {
        return readInputFromFile(new File(filePath));
    }
    public static TruckDroneDeliveryInput readInputFromFile(File f) throws IOException {
        Scanner myReader = new Scanner(f);
        int lineCounter = 0;
        TruckDroneDeliveryInput input = new TruckDroneDeliveryInput();
        List<Node> points = new ArrayList<>();
        while (myReader.hasNextLine()) {
            lineCounter++;
            String data = myReader.nextLine();
            if (data.startsWith("/")) {
                continue;
            }
            switch (lineCounter) {
                case 2:
                    Truck truck = new Truck();
                    truck.setSpeed(Double.parseDouble(data));
                    truck.setID("1");
                    truck.setCapacity(200);
                    input.setTruck(truck);
                    break;
                case 4:
                    Drone drone = new Drone();
                    drone.setCapacity(100);
                    drone.setID("1");
                    drone.setSpeed(Double.parseDouble(data));
                    drone.setDurationCapacity(100);
                    input.setDrone(drone);
                    break;
                case 6:
                    break;
                case 8:
                    String[] depotData = data.split(" ");
                    Depot depot = new Depot();
                    depot.setLocationID(depotData[2]);
                    input.setDepot(depot);
                    Node depotNode = new Node(Double.parseDouble(depotData[0]), Double.parseDouble(depotData[1]), depotData[2]);
                    points.add(depotNode);
                    break;
                default:
                    String[] locations = data.split(" ");
                    Node node = new Node();
                    node.setX(Double.parseDouble(locations[0]));
                    node.setY(Double.parseDouble(locations[1]));
                    node.setName(locations[2]);
                    points.add(node);
            }
        }
        myReader.close();
//        points.add(points.get(0)); // add a depot as finish node of all routes.
        input.setLocations(points);
        List<List<DistanceElement>> listDistances = new ArrayList<>();
        List<Request> listCustomerRequests = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            List<DistanceElement> distances = new ArrayList<>();
            for (int j = 0; j < points.size(); j++) {
                DistanceElement distanceElement = new DistanceElement();
                distanceElement.setId(i + "_" + j);
                distanceElement.setFromLocationId(points.get(i).getName());
                distanceElement.setToLocationId(points.get(j).getName());
//              double distance = Utils.calculateEuclideanDistance(points.get(i), points.get(j));
                double distance = Utils.calculateCoordinationDistance(points.get(i).getX(), points.get(i).getY(),
                        points.get(j).getX(), points.get(j).getY());
                distanceElement.setDistance(distance);
                distanceElement.setTravelTime(distance / input.getTruck().getSpeed());
                distanceElement.setFlyingTime(distance / input.getDrone().getSpeed());
                distances.add(distanceElement);
            }
            listDistances.add(distances);
            if (i == 0) continue;
            Request newRequest = new Request();
            newRequest.setEarliestTime("10h");
            newRequest.setLatestTime("11h");
            newRequest.setWeight(100);
            newRequest.setID(points.get(i).getName());
            newRequest.setLocationID(points.get(i).getName());
            listCustomerRequests.add(newRequest);
        }
        input.setDistances(listDistances);
        input.setRequests(listCustomerRequests);
        return input;
    }
}
