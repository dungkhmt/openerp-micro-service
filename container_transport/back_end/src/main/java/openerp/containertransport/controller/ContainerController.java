package openerp.containertransport.controller;

import lombok.RequiredArgsConstructor;
import openerp.containertransport.dto.ContainerFilterRequestDTO;
import openerp.containertransport.dto.ContainerModel;
import openerp.containertransport.service.ContainerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/container")
public class ContainerController {
    private final ContainerService containerService;
    @PostMapping("/create")
    public ResponseEntity<?> createContainer(@RequestBody ContainerModel containerModelDTO) {
        ContainerModel containerModel = containerService.createContainer(containerModelDTO);
        return ResponseEntity.status(HttpStatus.OK).body(containerModel);
    }

    @PostMapping("/")
    public ResponseEntity<?> filterContainer(@RequestBody ContainerFilterRequestDTO containerFilterRequestDTO){
        List<ContainerModel> containerModels = containerService.filterContainer(containerFilterRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(containerModels);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getContainerById (@PathVariable long id) {
        ContainerModel containerModel = containerService.getContainerById(id);
        return ResponseEntity.status(HttpStatus.OK).body(containerModel);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateContainer(@RequestBody ContainerModel containerModel) {
        ContainerModel containerModelUpdate = containerService.updateContainer(containerModel);
        return ResponseEntity.status(HttpStatus.OK).body(containerModelUpdate);
    }
}
