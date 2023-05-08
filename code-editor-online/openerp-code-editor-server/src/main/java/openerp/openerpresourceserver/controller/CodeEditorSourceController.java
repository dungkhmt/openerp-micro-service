package openerp.openerpresourceserver.controller;

import java.security.Principal;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.dto.CodeEditorSourceDTO;
import openerp.openerpresourceserver.entity.enumeration.ProgrammingLanguage;
import openerp.openerpresourceserver.exception.BadRequestAlertException;
import openerp.openerpresourceserver.service.CodeEditorSourceService;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/code-editor")
public class CodeEditorSourceController {
    private final CodeEditorSourceService codeEditorSourceService;

    @PostMapping(value = "/sources")
    public ResponseEntity<CodeEditorSourceDTO> createAndUpdateSource(Principal principal,
            @RequestBody CodeEditorSourceDTO codeEditorSourceDTO) {
        if (codeEditorSourceDTO.getRoomId() == null) {
            throw new BadRequestAlertException("Id phòng không được để trống");
        }
        if (codeEditorSourceDTO.getLanguage() == null) {
            throw new BadRequestAlertException("Ngôn ngữ lập trình không được để trống");
        }
        return ResponseEntity.ok().body(codeEditorSourceService.save(principal.getName(), codeEditorSourceDTO));
    }

    @GetMapping(value = "/sources/load-source")
    public ResponseEntity<CodeEditorSourceDTO> loadSource(Principal principal,
            @RequestParam(name = "roomId") UUID roomId,
            @RequestParam(name = "language") ProgrammingLanguage language) {
        return ResponseEntity.ok()
                .body(codeEditorSourceService.loadSourceByRoomIdAndLanguage(principal.getName(), roomId, language));
    }
}
