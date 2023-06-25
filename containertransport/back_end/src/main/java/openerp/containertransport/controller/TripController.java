package openerp.containertransport.controller;

import lombok.RequiredArgsConstructor;
import openerp.containertransport.constants.MetaData;
import openerp.containertransport.dto.TripCreateDTO;
import openerp.containertransport.dto.TripDeleteDTO;
import openerp.containertransport.dto.TripFilterRequestDTO;
import openerp.containertransport.dto.TripModel;
import openerp.containertransport.dto.metaData.MetaDTO;
import openerp.containertransport.dto.metaData.ResponseMetaData;
import openerp.containertransport.service.TripService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/trip")
public class TripController {
    private final TripService tripService;

    @PostMapping("/")
    public ResponseEntity<?> filterTrip(@RequestBody TripFilterRequestDTO requestDTO) {
        List<TripModel> tripModels = tripService.filterTrip(requestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS), tripModels));
    }

    @PostMapping("/{uid}")
    public ResponseEntity<?> getById(@PathVariable String uid) {
        TripModel tripModel = tripService.getByUid(uid);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS), tripModel));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createTrip(@RequestBody TripCreateDTO tripCreateDTO) {
        TripModel tripModelCreate = tripService.createTrip(tripCreateDTO.getTripContents(), tripCreateDTO.getShipmentId(), tripCreateDTO.getCreateBy());
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS), tripModelCreate));
    }

    @PostMapping ("/get-by-driver")
    public ResponseEntity<?> getTripsByDriver(@RequestBody TripFilterRequestDTO requestDTO, JwtAuthenticationToken token) {
        String username = token.getName();
        requestDTO.setUsername(username);
        List<TripModel> tripModels = tripService.getTripsByDriver(requestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS), tripModels));
    }

    @PutMapping("/update/{uid}")
    public ResponseEntity<?> updateTrip(@PathVariable String uid ,@RequestBody TripModel tripModel) {
        TripModel tripModelUpdate = tripService.updateTrip(uid, tripModel);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS), tripModelUpdate));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody TripDeleteDTO tripDeleteDTO) {
        List<TripModel> tripModels = tripService.deleteTrips(tripDeleteDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS), tripModels));
    }
}
