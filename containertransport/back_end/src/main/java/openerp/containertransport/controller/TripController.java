package openerp.containertransport.controller;

import lombok.RequiredArgsConstructor;
import openerp.containertransport.constants.MetaData;
import openerp.containertransport.dto.*;
import openerp.containertransport.dto.metaData.MetaDTO;
import openerp.containertransport.dto.metaData.ResponseMetaData;
import openerp.containertransport.dto.validDTO.ValidTripItemDTO;
import openerp.containertransport.entity.Shipment;
import openerp.containertransport.repo.ShipmentRepo;
import openerp.containertransport.service.TripService;
import openerp.containertransport.service.impl.TripServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/trip")
public class TripController {
    private final TripService tripService;
    private final TripServiceImpl tripServiceImpl;
    private final ShipmentRepo shipmentRepo;

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
        if(tripCreateDTO.getType().equals("Normal")) {
            TripModel tripModel = tripCreateDTO.getTripContents();
            List<TripItemModel> tripItemModels = tripModel.getTripItemModelList();
            Shipment shipment = shipmentRepo.findByUid(tripCreateDTO.getShipmentId());
            ValidTripItemDTO validTripItemDTO = tripServiceImpl.checkValidTrip(tripItemModels, shipment.getExecuted_time());
            if(!validTripItemDTO.getCheck()) {
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseMetaData(new MetaDTO(MetaData.BAD_REQUEST), validTripItemDTO.getMessageErr()));
            }
        }
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
    public ResponseEntity<?> updateTrip(@PathVariable String uid ,@RequestBody TripModel tripModel, JwtAuthenticationToken token) {
        List<String> roleIds = token
                .getAuthorities()
                .stream()
                .filter(grantedAuthority -> !grantedAuthority
                        .getAuthority()
                        .startsWith("ROLE_GR")) // remove all composite roles
                .map(grantedAuthority -> { // convert role to permission
                    String roleId = grantedAuthority.getAuthority().substring(5); // remove prefix "ROLE_"
                    return roleId;
                })
                .collect(Collectors.toList());
        if(roleIds.contains("ADMIN")) {
            List<TripItemModel> tripItemModels = tripModel.getTripItemModelList();
            Shipment shipment = shipmentRepo.findById(tripModel.getShipmentId()).get();
            ValidTripItemDTO validTripItemDTO = tripServiceImpl.checkValidTrip(tripItemModels, shipment.getExecuted_time());
            if(!validTripItemDTO.getCheck()) {
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseMetaData(new MetaDTO(MetaData.BAD_REQUEST), validTripItemDTO.getMessageErr()));
            }
            tripModel.setActor("ADMIN");
        }
        TripModel tripModelUpdate = tripService.updateTrip(uid, tripModel);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS), tripModelUpdate));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody TripDeleteDTO tripDeleteDTO) {
        List<TripModel> tripModels = tripService.deleteTrips(tripDeleteDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS), tripModels));
    }
}
