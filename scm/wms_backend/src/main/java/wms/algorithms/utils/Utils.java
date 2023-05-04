package wms.algorithms.utils;

import wms.algorithms.entity.Node;

public class Utils {
    public static double calculateEuclideanDistance(Node n1, Node n2) {
        double dx = n1.getX() - n2.getX();
        double dy = n1.getY() - n2.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }
    public static double calculateCoordinationDistance(double lat1, double lon1, double lat2, double lon2) {
        double r = 6371; // radius of the earth in km

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distance = r * c;

        return distance;
    }
//    public static double calculateEuclideanDistance(String lat1, String lon1, lat2, lon2) {
//        double dx = lat1 - lat2;
//        double dy = n1.getY() - n2.getY();
//        return Math.sqrt(dx * dx + dy * dy);
//    }
    // TODO: should have function to calculate distanceMatrix for whole set of node with (lat, long)
}