package openerp.openerpresourceserver.tarecruitment.controller;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.tarecruitment.service.TestCallApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
@AllArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/test-call-api")
public class TestCallApiController {

    private TestCallApi testCallApi;

    @GetMapping
    public ResponseEntity<?> testCallApi() {
        try {
            String access_token = testCallApi.getAccessToken();
            String token = "Bearer " + access_token;
            int responseCode = testCallApi.testCallApi(token);
            return ResponseEntity.ok().body("Successfully call API with response code is " + responseCode);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }
}
