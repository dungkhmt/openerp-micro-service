package openerp.containertransport.controller;

import lombok.RequiredArgsConstructor;
import openerp.containertransport.constants.MetaData;
import openerp.containertransport.dto.ContainerFilterRequestDTO;
import openerp.containertransport.dto.ContainerFilterRes;
import openerp.containertransport.dto.ContainerModel;
import openerp.containertransport.dto.metaData.MetaDTO;
import openerp.containertransport.dto.metaData.ResponseMetaData;
import openerp.containertransport.service.ContainerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/container")
public class ContainerController {
    private final ContainerService containerService;
    @PostMapping("/create")
    public ResponseEntity<?> createContainer(@RequestBody ContainerModel containerModelDTO, JwtAuthenticationToken token) {
        String username = token.getName();
//        ContainerModel containerModel = containerService.createContainer(containerModelDTO, username);
//        return ResponseEntity.status(HttpStatus.OK).body(containerModel);
        return containerService.createContainer(containerModelDTO, username);
    }

    @PostMapping("v2/create")
    public ResponseEntity<?> createv2(@RequestBody ContainerModel containerModel, JwtAuthenticationToken token) {
        String username = token.getName();
        return containerService.createContainerV2(containerModel, username);
    }

    @PostMapping("/")
    public ResponseEntity<?> filterContainer(@RequestBody ContainerFilterRequestDTO containerFilterRequestDTO, JwtAuthenticationToken token){
        String username = token.getName();
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
        if((roleIds.contains("TMS_CUSTOMER") || roleIds.contains("TMS_CUSTOMS")) && !Objects.equals(containerFilterRequestDTO.getType(), "Order")) {
            containerFilterRequestDTO.setOwner(username);
        }
        ContainerFilterRes containerModels = containerService.filterContainer(containerFilterRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS), containerModels));
    }

    @GetMapping("/{uid}")
    public ResponseEntity<?> getContainerById (@PathVariable String uid) {
        ContainerModel containerModel = containerService.getContainerByUid(uid);
        return ResponseEntity.status(HttpStatus.OK).body(containerModel);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateContainer(@RequestBody ContainerModel containerModel) {
        ContainerModel containerModelUpdate = containerService.updateContainer(containerModel);
        return ResponseEntity.status(HttpStatus.OK).body(containerModelUpdate);
    }

    @DeleteMapping("/delete/{uid}")
    public ResponseEntity<?> deleteContainer(@PathVariable String uid) {
        ContainerModel containerModelDelete = containerService.deleteContainer(uid);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS), containerModelDelete));
    }
}
