package com.example.shared.db.repo;


import com.example.shared.db.entities.EntityAuthorization;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntityAuthorizationRepo extends JpaRepository<EntityAuthorization, String> {

    List<EntityAuthorization> findAllByIdStartingWithAndRoleIdIn(String prefix, List<String> roleIds);
}
