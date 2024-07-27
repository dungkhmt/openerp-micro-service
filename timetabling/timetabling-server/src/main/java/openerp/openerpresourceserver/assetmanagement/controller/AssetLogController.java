package openerp.openerpresourceserver.assetmanagement.controller;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.assetmanagement.service.RequestLogService;
import openerp.openerpresourceserver.assetmanagement.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
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
