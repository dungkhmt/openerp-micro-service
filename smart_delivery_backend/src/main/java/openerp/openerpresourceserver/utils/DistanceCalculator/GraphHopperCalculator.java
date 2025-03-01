package openerp.openerpresourceserver.utils.DistanceCalculator;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.ResponsePath;
import com.graphhopper.config.Profile;
import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Component
public class GraphHopperCalculator  {

    private final GraphHopper graphHopper;

    public GraphHopperCalculator(@Value("${osm-file-path}") String osmFilePath) {
        this.graphHopper = new GraphHopper();

        graphHopper.setProfiles(
                new Profile("car")
                        .setVehicle("car")
                        .setWeighting("fastest")
                        .setTurnCosts(false)
        );

        graphHopper.setOSMFile(osmFilePath);
        graphHopper.setGraphHopperLocation("target/routing-graph-vietnam-latest-cache");
        graphHopper.importOrLoad();
    }


    public double calculateRealDistanceFromTo(double fromLat, double fromLon, double toLat, double toLon) {
//        log.info("Start calculate path 1" + fromLat +" " + fromLon + " " + toLat + " " + toLon);
        GHRequest request = new GHRequest(fromLat, fromLon, toLat, toLon)
                .setProfile("car").setLocale(Locale.US);
        GHResponse response = graphHopper.route(request);
        ResponsePath path = response.getBest();
        if (path == null) {
            throw new NotFoundException(String.format("Not path found from Point(lat:%f, lon:%f) to Point(lat:%f, lon:%f)", fromLat, fromLon, toLat, toLon));
        }
        return path.getDistance();
    }

    private double roundBigDecimal(BigDecimal b) {
        return Precision.round(b.doubleValue(), 6);
    }

    private double roundDouble(double b) {
        return Precision.round(b, 6);
    }

}