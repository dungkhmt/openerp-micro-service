package openerp.openerpresourceserver.repo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import openerp.openerpresourceserver.entity.CodeEditorSource;
import openerp.openerpresourceserver.entity.enumeration.ProgrammingLanguage;

@Repository
public interface CodeEditorSourceRepository extends JpaRepository<CodeEditorSource, UUID> {
    Optional<CodeEditorSource> findByRoomIdAndLanguage(UUID roomId,ProgrammingLanguage language);

    void deleteByRoomId(UUID roomId);
}
