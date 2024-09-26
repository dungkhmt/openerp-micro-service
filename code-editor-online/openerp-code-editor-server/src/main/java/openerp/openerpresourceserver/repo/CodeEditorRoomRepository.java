package openerp.openerpresourceserver.repo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import openerp.openerpresourceserver.entity.CodeEditorRoom;

@Repository
public interface CodeEditorRoomRepository extends JpaRepository<CodeEditorRoom, UUID> ,JpaSpecificationExecutor<CodeEditorRoom>{

}
