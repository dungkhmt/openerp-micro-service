package openerp.openerpresourceserver.controller;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.controller.vm.ShareRoomVM;
import openerp.openerpresourceserver.dto.CodeEditorRoomDTO;
import openerp.openerpresourceserver.dto.SharedRoomUserDTO;
import openerp.openerpresourceserver.exception.BadRequestAlertException;
import openerp.openerpresourceserver.service.CodeEditorRoomService;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/code-editor")
public class CodeEditorRoomController {
    private final CodeEditorRoomService codeEditorRoomService;

    @GetMapping(value = "/rooms/search")
    public ResponseEntity<Page<CodeEditorRoomDTO>> searchRoom(Principal principal,
            @RequestParam(name = "keyword", required = false) String keyword,
            Pageable pageable) {
        return ResponseEntity.ok().body(codeEditorRoomService.search(principal.getName(), keyword, pageable));
    }

    @PostMapping(value = "/rooms")
    public ResponseEntity<CodeEditorRoomDTO> createRoom(Principal principal,
            @RequestBody CodeEditorRoomDTO codeEditorRoomDTO) {
        String userLoginId = principal.getName();
        CodeEditorRoomDTO result = codeEditorRoomService.create(userLoginId, codeEditorRoomDTO);
        return ResponseEntity.ok().body(result);
    }

    @GetMapping(value = "/rooms/{id}")
    public ResponseEntity<CodeEditorRoomDTO> getRoomById(@PathVariable(name = "id") String id) {
        try {
            return ResponseEntity.ok().body(codeEditorRoomService.findById(UUID.fromString(id)));
        } catch (IllegalArgumentException e) {
            throw new BadRequestAlertException("Invalid room id: " + id);
        }
    }

    @PutMapping(value = "/rooms/{id}")
    public ResponseEntity<CodeEditorRoomDTO> updateRoom(@PathVariable(name = "id") UUID id,
            @RequestBody CodeEditorRoomDTO codeEditorRoomDTO) {
        return ResponseEntity.ok().body(codeEditorRoomService.update(id, codeEditorRoomDTO));
    }

    @DeleteMapping(value = "/rooms/{id}")
    public ResponseEntity<Void> deleteRoomById(@PathVariable(name = "id") UUID id) {
        codeEditorRoomService.deleteById(id);
        return ResponseEntity.ok().body(null);
    }

    @PutMapping(value = "/rooms/share")
    public ResponseEntity<Void> shareRoom(@RequestBody ShareRoomVM shareRoomVM) {
        codeEditorRoomService.shareRoom(shareRoomVM);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping(value = "/rooms/{roomId}/shared-users")
    public ResponseEntity<List<SharedRoomUserDTO>> getSharedUsersOfRoom(@PathVariable(name = "roomId") UUID roomId) {
        List<SharedRoomUserDTO> sharedUsers = codeEditorRoomService.getSharedUsersOfRoom(roomId);
        return ResponseEntity.ok().body(sharedUsers);
    }

    @DeleteMapping(value = "/rooms/{roomId}/access-permission/{userId}")
    public ResponseEntity<Void> deleteAccessPermission(@PathVariable(name = "roomId") UUID roomId,
            @PathVariable(name = "userId") String userId) {
        codeEditorRoomService.deleteAccessPermission(roomId, userId);
        return ResponseEntity.ok().body(null);
    }
}
