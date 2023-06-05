package openerp.containertransport.controller;

import lombok.RequiredArgsConstructor;
import openerp.containertransport.constants.MetaData;
import openerp.containertransport.dto.TypeContainerFilterReqDTO;
import openerp.containertransport.dto.TypeContainerFilterRes;
import openerp.containertransport.dto.TypeContainerModel;
import openerp.containertransport.dto.metaData.MetaDTO;
import openerp.containertransport.dto.metaData.ResponseMetaData;
import openerp.containertransport.service.TypeContainerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/type/container")
public class TypeContainerController {
    private final TypeContainerService typeContainerService;

    @PostMapping("/create")
    public ResponseEntity<?> createTypeContainer (@RequestBody TypeContainerModel typeContainerModel) {
        TypeContainerModel typeContainerModelCreate = typeContainerService.createTypeContainer(typeContainerModel);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS), typeContainerModelCreate));
    }

    @PostMapping("/")
    public ResponseEntity<?> filterTypeContainer (@RequestBody TypeContainerFilterReqDTO typeContainerFilterReqDTO) {
        TypeContainerFilterRes typeContainerFilterRes = typeContainerService.filterTypeContainer(typeContainerFilterReqDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS), typeContainerFilterRes));

    }
}
