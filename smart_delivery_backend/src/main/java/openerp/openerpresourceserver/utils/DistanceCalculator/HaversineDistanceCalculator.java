package openerp.openerpresourceserver.utils.DistanceCalculator;

public class HaversineDistanceCalculator {
    public static Double calculateDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        final double R = 6371; // Bán kính Trái Đất tính bằng km
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        // Tính các chênh lệch về vĩ độ và kinh độ
        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;

        // Công thức Haversine
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Tính khoảng cách theo km
        double distance = R * c;
        return distance;
    }
}
