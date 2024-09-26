package openerp.openerpresourceserver.repo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import openerp.openerpresourceserver.entity.SharedRoomUser;
import openerp.openerpresourceserver.entity.SharedRoomUserKey;

@Repository
public interface SharedRoomUserRepository extends JpaRepository<SharedRoomUser, SharedRoomUserKey> {
    Optional<SharedRoomUser> findByIdRoomIdAndIdUserId(UUID roomId, String userId);

    void deleteByIdRoomIdAndIdUserId(UUID roomId, String userId);

    void deleteByIdRoomId(UUID roomId);

    List<SharedRoomUser> findByIdRoomId(UUID roomId);
}
