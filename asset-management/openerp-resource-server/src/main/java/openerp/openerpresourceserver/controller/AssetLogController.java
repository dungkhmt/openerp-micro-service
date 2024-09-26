package openerp.openerpresourceserver.controller;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.service.RequestLogService;
import openerp.openerpresourceserver.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("asset-log")
public class AssetLogController {
    private RequestService requestService;
    private RequestLogService requestLogService;

//    public ResponseEntity<?> get
}
