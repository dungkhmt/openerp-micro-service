package openerp.openerpresourceserver.repo;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import openerp.openerpresourceserver.entity.HomestayUser;

import java.util.List;
import java.util.Optional;

@Repository
public interface HomestayUserRepository extends JpaRepository<HomestayUser, Long> {
    Optional<HomestayUser> findByEmail(String email);

    List<HomestayUser> findByUserType(String userType);

    
}
