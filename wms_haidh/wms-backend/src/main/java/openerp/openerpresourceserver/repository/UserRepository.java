package openerp.openerpresourceserver.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import openerp.openerpresourceserver.entity.User;

public interface UserRepository extends JpaRepository<User, String> {
	Optional<User> findByEmail(String email);
}
