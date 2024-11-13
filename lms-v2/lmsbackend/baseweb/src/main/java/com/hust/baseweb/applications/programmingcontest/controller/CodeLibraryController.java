package com.hust.baseweb.applications.programmingcontest.controller;

import com.hust.baseweb.applications.programmingcontest.model.LibraryResponseDTO;
import com.hust.baseweb.applications.programmingcontest.model.ModelCreateLibrary;
import com.hust.baseweb.applications.programmingcontest.entity.ProgrammingParticipantLibrary;
import com.hust.baseweb.applications.programmingcontest.service.CodeLibraryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@CrossOrigin
@AllArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/code-library")
public class CodeLibraryController {

    private final CodeLibraryService codeLibraryService;

    @PostMapping("/create-library")
    public ResponseEntity<?> createLibrary(@RequestBody ModelCreateLibrary modelCreateLibrary, Principal principal) {
        try {
            ProgrammingParticipantLibrary createdLibrary = codeLibraryService.createLibrary(modelCreateLibrary, principal.getName());
            return ResponseEntity.ok(createdLibrary);
        } catch (Exception e) {
            log.error("Error creating library: ", e);
            return ResponseEntity.badRequest().body("Failed to create library");
        }
    }

    @GetMapping("/student-list-library/{userId}")
    public ResponseEntity<?> getStudentLibraries(@PathVariable String userId) {
        try {
            List<LibraryResponseDTO> libraries = codeLibraryService.getLibrariesByUser(userId);
            return ResponseEntity.ok(libraries);
        } catch (Exception e) {
            log.error("Error retrieving libraries: ", e);
            return ResponseEntity.badRequest().body("Failed to retrieve libraries");
        }
    }

    @GetMapping("/get-user-info")
    public ResponseEntity<?> getUserInfo(Principal principal) {
        String userId = principal.getName();
        return ResponseEntity.ok(userId);
    }

    @PutMapping("/edit-library/{id}")
    public ResponseEntity<?> editStudentLibrary(@PathVariable UUID id, @RequestBody ModelCreateLibrary modelCreateLibrary) {
        try {
            ProgrammingParticipantLibrary updatedLibrary = codeLibraryService.editLibrary(id, modelCreateLibrary);
            return ResponseEntity.ok(updatedLibrary);
        } catch (Exception e) {
            log.error("Error editing library: ", e);
            return ResponseEntity.badRequest().body("Failed to edit library");
        }
    }

    @DeleteMapping("/delete-library/{id}")
    public ResponseEntity<?> deleteStudentLibrary(@PathVariable UUID id) {
        try {
            codeLibraryService.deleteLibrary(id);
            return ResponseEntity.ok("Library deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting library: ", e);
            return ResponseEntity.badRequest().body("Failed to delete library");
        }
    }
}
