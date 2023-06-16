package openerp.containertransport.controller;

import lombok.RequiredArgsConstructor;
import openerp.containertransport.constants.MetaData;
import openerp.containertransport.dto.TripItemModel;
import openerp.containertransport.dto.metaData.MetaDTO;
import openerp.containertransport.dto.metaData.ResponseMetaData;
import openerp.containertransport.service.TripItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/tripItem")
public class TripItemController {
    private final TripItemService tripItemService;

    @PostMapping("/{id}")
    public ResponseEntity<?> getTripItemByTripId (@PathVariable long id) {
        List<TripItemModel> tripItemModels = tripItemService.getTripItemByTripId(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS), tripItemModels));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateTripItem (@PathVariable Long id, @RequestBody TripItemModel tripItemModel) {
        TripItemModel tripItemModelUpdate = tripItemService.updateTripItem(id, tripItemModel);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS), tripItemModelUpdate));
    }
}
