package openerp.containertransport.controller;

import lombok.RequiredArgsConstructor;
import openerp.containertransport.constants.MetaData;
import openerp.containertransport.dto.TripCreateDTO;
import openerp.containertransport.dto.TripFilterRequestDTO;
import openerp.containertransport.dto.TripModel;
import openerp.containertransport.dto.metaData.MetaDTO;
import openerp.containertransport.dto.metaData.ResponseMetaData;
import openerp.containertransport.service.TripService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

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

    @PostMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable long id) {
        TripModel tripModel = tripService.getById(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS), tripModel));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createTrip(@RequestBody TripCreateDTO tripCreateDTO) {
        TripModel tripModelCreate = tripService.createTrip(tripCreateDTO.getTripContents(), tripCreateDTO.getShipmentId(), tripCreateDTO.getCreateBy());
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS), tripModelCreate));
    }
}
