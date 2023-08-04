package com.hust.wmsbackend.vrp.delivery;

import com.graphhopper.ResponsePath;
import com.hust.wmsbackend.management.auto.DistanceCalculator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omg.SendingContext.RunTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class DeliveryRouteServiceImpl implements DeliveryRouteService {

    DistanceCalculator distanceCalculator;
    private Map<AddressPair, ResponsePath> pathMap = new HashMap<>();
    private final Double INFINITY_VALUE = Double.MAX_VALUE;

    @Override
    public RouteResponse getRoute(RouteRequest r) throws RuntimeException {
        // remove duplicate customer address
        buildNormAddressList(r);
        List<Double[]> matrix = calCostMatrix(r);
        log.info("Cost matrix => ");
        for (Double[] m : matrix) {
            log.info(Arrays.toString(m));
        }
        List<Integer> path = new ArrayList<>();
        List<List<Integer>> totalPaths = new ArrayList<>();
        tsp(0, matrix.size(), path, matrix, ReducedMatrix.builder().matrix(matrix).build(), totalPaths);
        // TODO: Get only first path
        if (totalPaths.size() > 0) {
            RouteResponse response = new RouteResponse();
            List<Integer> optPath = totalPaths.get(0);

            double totalCost = 0.0;
            List<ResponsePath> responsePath = new ArrayList<>();
            List<DeliveryAddressDTO> order = new ArrayList<>();
            int sequence = 1;
            for (int i = 0; i < optPath.size(); i++) {
                int sourceNode = optPath.get(i);
                int destNode = i != optPath.size() - 1 ? optPath.get(i + 1) : 0;

                // calculate total cost
                totalCost += matrix.get(sourceNode)[destNode];

                // get path in node
                AddressPair pair = new AddressPair();
                if (sourceNode == 0) {
                    pair.setSourLon(r.getWarehouseLon());
                    pair.setSourLat(r.getWarehouseLat());
                } else {
                    pair.setSourLon(r.getAddressDTOs().get(sourceNode - 1).getLongitude());
                    pair.setSourLat(r.getAddressDTOs().get(sourceNode - 1).getLatitude());
                }
                if (destNode == 0) {
                    pair.setDestLon(r.getWarehouseLon());
                    pair.setDestLat(r.getWarehouseLat());
                } else {
                    pair.setDestLon(r.getAddressDTOs().get(destNode - 1).getLongitude());
                    pair.setDestLat(r.getAddressDTOs().get(destNode - 1).getLatitude());
                }
                ResponsePath adderPath = pathMap.get(pair);
                responsePath.add(adderPath);

                // set sequence to delivery address DTO
                if (sourceNode == 0) {
                    continue;
                } else {
                    DeliveryAddressDTO addressDTO = r.getAddressDTOs().get(sourceNode - 1);
                    addressDTO.setSequence(sequence);
                    order.add(addressDTO);
                    sequence += 1;
                }
            }
//            totalCost += matrix.get(optPath.get(optPath.size() - 1))[0]; // cost from last node to warehouse
            response.setTotalCost(totalCost);
            response.setPaths(responsePath);
            response.setOrder(order);
            return response;
        }
        return null;
    }

    private void buildNormAddressList(RouteRequest r) {
        List<DeliveryAddressDTO> originAddress = r.getAddressDTOs();
        log.info(String.format("Origin address => %s", originAddress));
        List<DeliveryAddressDTO> normAddress = new ArrayList<>();
        normAddress.addAll(originAddress);
        Set<Integer> duplicateIndex = new HashSet<>();
        for (int i = 0; i < originAddress.size() - 1; i++) {
            for (int j = i + 1; j < originAddress.size(); j++) {
                if (originAddress.get(i).getLatitude().compareTo(originAddress.get(j).getLatitude()) == 0 &&
                    originAddress.get(i).getLongitude().compareTo(originAddress.get(j).getLongitude()) == 0) {
                    duplicateIndex.add(j);
                }
            }
        }
        List<Integer> sortedIndex = new ArrayList<>(duplicateIndex);
        Collections.sort(sortedIndex);
        for (int i = sortedIndex.size() - 1; i >= 0; i--) {
            normAddress.remove(i);
        }
        r.setAddressDTOs(normAddress);
        log.info(String.format("Norm address => %s", normAddress));
    }

    @Override
    public void tsp(int level, int totalNode, List<Integer> path, List<Double[]> costMatrix, ReducedMatrix reducedMatrix, List<List<Integer>> totalPaths) {
        if (level == 0) {
            path.add(0); // warehouse
            ReducedMatrix matrix = getReducedMatrix(reducedMatrix);
            tsp(level + 1, totalNode, path, costMatrix, matrix, totalPaths);
        }

        if (level == totalNode - 1 && path.size() == totalNode) {
            log.info(String.format("Save path %s", path));
            List<Integer> adder = new ArrayList<>(path);
            totalPaths.add(adder);
            return;
        }

        ReducedMatrix minReducedMatrix = new ReducedMatrix();
        int nextNode = -1;
        double minCost = Double.MAX_VALUE;

        for (int i = 0; i < totalNode; i++) {
            // if i in path => continue
            if (path.contains(i)) {
                continue;
            }

            ReducedMatrix cloneReducedMatrix;
            try {
                cloneReducedMatrix = reducedMatrix.clone();
            } catch (CloneNotSupportedException e) {
                log.warn("Not cloneable");
                return;
            }
            cloneReducedMatrix.setInfinityValue(i, path.get(path.size() - 1));
            setInfinityValueForColAndRow(i, path.get(path.size() - 1), cloneReducedMatrix);

            ReducedMatrix matrix = getReducedMatrix(cloneReducedMatrix);
            double comp = matrix.getReducedCost() + reducedMatrix.getMatrix().get(path.get(path.size() - 1))[i];
            if (comp < minCost) {
                minReducedMatrix.setReducedCost(matrix.getReducedCost());
                minReducedMatrix.setMatrix(matrix.getMatrix());
                minCost = comp;
                nextNode = i;
            }
        }

        if (nextNode == -1) {
            if (path.size() == totalNode) {
                log.info(String.format("Save path %s", path));
                List<Integer> adder = new ArrayList<>(path);
                totalPaths.add(adder);
                return;
            }
            throw new RuntimeException(String.format(
                "Can not find next node for level %s, totalNode %s, path %s, costMatrix %s, reducedMatrix %s",
                level, totalNode, path, costMatrix, reducedMatrix));
        }

        path.add(nextNode);
        tsp(level + 1, totalNode, path, costMatrix, minReducedMatrix, totalPaths);
        path.remove(path.size() - 1);
    }

    private void setInfinityValueForColAndRow(int col, int row, ReducedMatrix reducedMatrix) {
        try {
            int matrixSize = reducedMatrix.getMatrix().size();
            for (int i = 0; i < matrixSize; i++) {
                if (i == row) {
                    Double[] newRow = new Double[matrixSize];
                    Arrays.setAll(newRow, e -> INFINITY_VALUE);
                    reducedMatrix.getMatrix().set(row, newRow);
                }
                for (int j = 0; j < matrixSize; j++) {
                    if (j == col) {
                        reducedMatrix.getMatrix().get(i)[j] = INFINITY_VALUE;
                    }
                }
            }
        } catch (Exception e) {
            log.warn(String.format("Can not set infinity value for col %d row %d", col, row));
        }
    }

    public ReducedMatrix getReducedMatrix(ReducedMatrix originMatrix) {
        ReducedMatrix matrix;
        try {
             matrix = originMatrix.clone();
        } catch (CloneNotSupportedException e) {
            log.warn("Not support clone");
            return null;
        }
        double totalReduceCost = 0.0;

        // update row
        for (int i = 0; i < matrix.getMatrix().size(); i++) {
            double minRowValue = Double.MAX_VALUE;
            for (Double d : matrix.getMatrix().get(i)) {
                if (d < minRowValue) {
                    minRowValue = d;
                }
            }
            if (minRowValue != Double.MAX_VALUE) {
                totalReduceCost += minRowValue;
                matrix.subtractRow(i, minRowValue);
            }
        }

        // update column
        for (int i = 0; i < matrix.getMatrix().size(); i++) {
            double minColValue = Double.MAX_VALUE;
            for (int j = 0; j < matrix.getMatrix().size(); j++) {
                if (matrix.getMatrix().get(j)[i] < minColValue) {
                    minColValue = matrix.getMatrix().get(j)[i];
                }
            }
            if (minColValue != Double.MAX_VALUE){
                totalReduceCost += minColValue;
                matrix.subtractColumn(i, minColValue);
            }
        }
        return ReducedMatrix.builder().reducedCost(totalReduceCost).matrix(matrix.getMatrix()).build();
    }

    private List<Double[]> calCostMatrix(RouteRequest r) throws RuntimeException {
        List<Double[]> matrix = new ArrayList<>();
        int customerCount = r.getAddressDTOs().size();
        log.info(String.format("Start calculate cost matrix for request %s", r));

        // calculate cost from warehouse to another custom addresses
        Double[] warehouseToCustomer = new Double[customerCount + 1]; // add 1 because of warehouse
        warehouseToCustomer[0] = INFINITY_VALUE;
        for (int i = 0; i < customerCount; i++) {
            DeliveryAddressDTO addressDTO = r.getAddressDTOs().get(i);
            ResponsePath responsePath = distanceCalculator.calculate(r.getWarehouseLat(), r.getWarehouseLon(),
                addressDTO.getLatitude(), addressDTO.getLongitude());
            warehouseToCustomer[i + 1] = responsePath != null ? responsePath.getDistance() : INFINITY_VALUE;
            pathMap.put(AddressPair.builder()
                .sourLat(r.getWarehouseLat()).sourLon(r.getWarehouseLon())
                .destLat(addressDTO.getLatitude()).destLon(addressDTO.getLongitude())
                .build(), responsePath);
        }
        matrix.add(warehouseToCustomer);

        // calculate cost from address to another
        for (DeliveryAddressDTO addressDTO : r.getAddressDTOs()) {
            Double[] customerToCustomer = new Double[customerCount + 1];
            ResponsePath toWarehousePath = distanceCalculator.calculate(addressDTO.getLatitude(), addressDTO.getLongitude(),
                r.getWarehouseLat(), r.getWarehouseLon()); // distance from this address to source warehouse
            customerToCustomer[0] = toWarehousePath.getDistance();
            pathMap.put(AddressPair.builder().destLon(r.getWarehouseLon()).destLat(r.getWarehouseLat())
                            .sourLon(addressDTO.getLongitude()).sourLat(addressDTO.getLatitude()).build(), toWarehousePath);
            for (int i = 0; i < customerCount; i++) {
                DeliveryAddressDTO destAddressDTO = r.getAddressDTOs().get(i);
                if (destAddressDTO.compareTo(addressDTO) == 0) {
                    customerToCustomer[i + 1] = INFINITY_VALUE;
                    continue;
                }
                ResponsePath toCustomerPath = distanceCalculator.calculate(addressDTO.getLatitude(), addressDTO.getLongitude(),
                                                                           destAddressDTO.getLatitude(), destAddressDTO.getLongitude());
                customerToCustomer[i + 1] = toCustomerPath != null ? toCustomerPath.getDistance() : INFINITY_VALUE;
                pathMap.put(AddressPair.builder().sourLat(addressDTO.getLatitude()).sourLon(addressDTO.getLongitude())
                                .destLat(destAddressDTO.getLatitude()).destLon(destAddressDTO.getLongitude()).build(), toCustomerPath);
            }
            matrix.add(customerToCustomer);
        }

        // calculate cost from customer addresses to source warehouse
//        Double[] customerToWarehouse = new Double[customerCount + 1];
//        for (int i = 0; i < customerCount; i++) {
//            DeliveryAddressDTO addressDTO = r.getAddressDTOs().get(i);
//            ResponsePath toWarehousePath = distanceCalculator.calculate(addressDTO.getLatitude(),
//                                                                        addressDTO.getLongitude(),
//                                                                        r.getWarehouseLat(),
//                                                                        r.getWarehouseLon());
//            customerToWarehouse[i] = toWarehousePath != null ? toWarehousePath.getDistance() : INFINITY_VALUE;
//            pathMap.put(AddressPair.builder().sourLon(addressDTO.getLongitude()).sourLat(addressDTO.getLatitude())
//                            .destLon(r.getWarehouseLon()).destLat(r.getWarehouseLat()).build(), toWarehousePath);
//        }
//        customerToWarehouse[customerCount] = INFINITY_VALUE; // cost from warehouse to warehouse
//        matrix.add(customerToWarehouse);
        return matrix;
    }

}
