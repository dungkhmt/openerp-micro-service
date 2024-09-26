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

    @PostMapping("/{tripId}")
    public ResponseEntity<?> getTripItemByTripId (@PathVariable String tripId) {
        List<TripItemModel> tripItemModels = tripItemService.getTripItemByTripId(tripId);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS), tripItemModels));
    }

    @PutMapping("/update/{uid}")
    public ResponseEntity<?> updateTripItem (@PathVariable String uid, @RequestBody TripItemModel tripItemModel) {
        TripItemModel tripItemModelUpdate = tripItemService.updateTripItem(uid, tripItemModel);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS), tripItemModelUpdate));
    }

    @DeleteMapping("/delete/{uid}")
    public ResponseEntity<?> deleteTripItem (@PathVariable String uid) {
        TripItemModel tripItemModelDelete = tripItemService.deleteTripItem(uid);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS), tripItemModelDelete));
    }
}
