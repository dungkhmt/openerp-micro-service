package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.entity.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizQuestionRepo extends JpaRepository<QuizQuestion,String> {
    long count();
}
