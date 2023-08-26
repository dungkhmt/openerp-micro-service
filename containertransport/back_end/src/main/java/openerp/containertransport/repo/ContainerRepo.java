package openerp.containertransport.repo;

import openerp.containertransport.entity.Container;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContainerRepo extends JpaRepository<Container, Long> {
    Container findById(long id);
    Container findByUid(String uid);
    Container findByContainerCode(String containerCode);
}
