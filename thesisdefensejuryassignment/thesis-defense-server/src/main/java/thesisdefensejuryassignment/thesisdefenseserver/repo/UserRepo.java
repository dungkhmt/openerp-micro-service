package thesisdefensejuryassignment.thesisdefenseserver.repo;


import thesisdefensejuryassignment.thesisdefenseserver.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, String> {

}
