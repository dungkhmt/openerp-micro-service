package openerp.containertransport.utils;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.ResponsePath;
import com.graphhopper.config.Profile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.apache.commons.math3.util.Precision;

import java.math.BigDecimal;
import java.util.Locale;

@Component
public class GraphHopperCalculator {
    private final GraphHopper graphHopper;

    public GraphHopperCalculator(@Value("${osm-file-path}") String osmFilePath) {
        this.graphHopper = new GraphHopper();
        graphHopper.setProfiles(new Profile("car").setVehicle("car").setWeighting("fastest").setTurnCosts(false));
        graphHopper.setOSMFile(osmFilePath);
        graphHopper.setGraphHopperLocation("target/routing-graph-vietnam-latest-cache");
        graphHopper.importOrLoad();
    }
    public ResponsePath calculate(BigDecimal fromLat, BigDecimal fromLon, BigDecimal toLat, BigDecimal toLon) throws Exception {
        GHRequest request = new GHRequest(roundBigDecimal(fromLat), roundBigDecimal(fromLon),
                roundBigDecimal(toLat), roundBigDecimal(toLon))
                .setProfile("car").setLocale(Locale.US);
        GHResponse response = graphHopper.route(request);
        ResponsePath path = response.getBest();
        if (path == null) {
            throw new Exception(String.format("Not path found from Point(lat:%f, lon:%f) to Point(lat:%f, lon:%f)", fromLat, fromLon, toLat, toLon));
        }
        return path;
    }

    private double roundBigDecimal(BigDecimal b) {
        return Precision.round(b.doubleValue(), 6);
    }
}
