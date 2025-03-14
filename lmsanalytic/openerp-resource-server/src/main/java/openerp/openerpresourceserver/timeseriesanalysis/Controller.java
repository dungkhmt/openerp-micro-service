package openerp.openerpresourceserver.timeseriesanalysis;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@Log4j2
@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
public class Controller {
    @PostMapping("/time-series-predict")
    public ResponseEntity<?> timeSeriesPredict(Principal principal, @RequestBody ModelInputTimeSeriesPrediction I){
        log.info("timeSeriesPredict, user = " + principal.getName());
        MovingAverage MA = new MovingAverage(I.getWindowsSize(),I.getSequences());
        double val = MA.predictNextValue();
        log.info("timeSeriesPredict, res = " + val);
        ModelResponseTimeSeriesPrediction res = new ModelResponseTimeSeriesPrediction(val);
        return ResponseEntity.ok().body(res);
    }
}
