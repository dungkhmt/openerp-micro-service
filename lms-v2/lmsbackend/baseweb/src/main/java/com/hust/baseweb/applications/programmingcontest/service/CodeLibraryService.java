package com.hust.baseweb.applications.programmingcontest.service;

import com.hust.baseweb.applications.programmingcontest.model.LibraryResponseDTO;
import com.hust.baseweb.applications.programmingcontest.model.ModelCreateLibrary;
import com.hust.baseweb.applications.programmingcontest.entity.ProgrammingParticipantLibrary;
import com.hust.baseweb.applications.programmingcontest.repo.ProgrammingParticipantLibraryRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CodeLibraryService {

    private final ProgrammingParticipantLibraryRepository libraryRepository;

    public ProgrammingParticipantLibrary createLibrary(ModelCreateLibrary modelCreateLibrary, String userId) {
        ProgrammingParticipantLibrary library = new ProgrammingParticipantLibrary();
        library.setUserId(userId);
        library.setName(modelCreateLibrary.getName());
        library.setLanguage(modelCreateLibrary.getLanguage());
        library.setContent(modelCreateLibrary.getContent());
        library.setStatus(modelCreateLibrary.getStatus());
        library.setCreatedStamp(modelCreateLibrary.getCreatedStamp());
        library.setLastUpdatedStamp(modelCreateLibrary.getLastUpdatedStamp());
        return libraryRepository.save(library);
    }

    public List<LibraryResponseDTO> getLibrariesByUser(String userId) {
        List<ProgrammingParticipantLibrary> libraries = libraryRepository.findByUserId(userId);
        return libraries.stream()
                        .map(lib -> new LibraryResponseDTO(lib.getId(), lib.getName(), lib.getLanguage(), lib.getContent(), lib.getStatus(), lib.getLastUpdatedStamp()))
                        .collect(Collectors.toList());
    }

    public ProgrammingParticipantLibrary editLibrary(UUID id, ModelCreateLibrary modelCreateLibrary) {
        Optional<ProgrammingParticipantLibrary> libraryOpt = libraryRepository.findById(id);
        if (libraryOpt.isPresent()) {
            ProgrammingParticipantLibrary library = libraryOpt.get();
            library.setName(modelCreateLibrary.getName());
            library.setLanguage(modelCreateLibrary.getLanguage());
            library.setContent(modelCreateLibrary.getContent());
            library.setStatus(modelCreateLibrary.getStatus());
            return libraryRepository.save(library);
        }
        throw new RuntimeException("Library not found");
    }

    public void deleteLibrary(UUID id) {
        libraryRepository.deleteById(id);
    }
}
