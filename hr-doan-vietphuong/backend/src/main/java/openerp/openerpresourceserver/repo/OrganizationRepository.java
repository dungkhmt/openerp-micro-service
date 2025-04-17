package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface OrganizationRepository extends JpaRepository<Organization, Long>, JpaSpecificationExecutor<Organization> {
    List<Organization> findAllAndByStatus(int status);

    boolean existsByName(String name);

    boolean existsByIdNotAndName(Long id, String name);
}

