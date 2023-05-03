package wms.algorithms.utils;

import wms.algorithms.entity.Node;

public class Utils {
    public static double calculateEuclideanDistance(Node n1, Node n2) {
        double dx = n1.getX() - n2.getX();
        double dy = n1.getY() - n2.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }
    // TODO: should have function to calculate distanceMatrix for whole set of node with (lat, long)
}