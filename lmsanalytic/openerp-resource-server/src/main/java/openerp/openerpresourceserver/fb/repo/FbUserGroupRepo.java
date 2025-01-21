package openerp.openerpresourceserver.fb.repo;

import openerp.openerpresourceserver.fb.entity.CompositeUserGroupId;
import openerp.openerpresourceserver.fb.entity.FbUserGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FbUserGroupRepo extends JpaRepository<FbUserGroup, CompositeUserGroupId> {
    FbUserGroup findByUserIdAndGroupId(String userId, String groupId);
}
