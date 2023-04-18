package openerp.containertransport.controller;

import lombok.RequiredArgsConstructor;
import openerp.containertransport.dto.TrailerFilterRequestDTO;
import openerp.containertransport.dto.TrailerModel;
import openerp.containertransport.service.TrailerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/trailer")
public class TrailerController {
    public final TrailerService trailerService;

    @PostMapping("/create")
    public ResponseEntity<?> createTrailer(@RequestBody TrailerModel trailerModel){
        TrailerModel trailerModelCreate = trailerService.createTrailer(trailerModel);
        return ResponseEntity.status(HttpStatus.OK).body(trailerModelCreate);
    }

    @PostMapping("/")
    public ResponseEntity<?> filterTrailer(@RequestBody TrailerFilterRequestDTO trailerFilterRequestDTO) {
        List<TrailerModel> trailerModels = trailerService.filterTrailer(trailerFilterRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(trailerModels);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTrailerById(@PathVariable long id) {
        TrailerModel trailerModel = trailerService.getTrailerById(id);
        return ResponseEntity.status(HttpStatus.OK).body(trailerModel);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateTrailer(@RequestBody TrailerModel trailerModel) {
        TrailerModel trailerModelUpdate = trailerService.updateTrailer(trailerModel);
        return ResponseEntity.status(HttpStatus.OK).body(trailerModelUpdate);
    }
}
