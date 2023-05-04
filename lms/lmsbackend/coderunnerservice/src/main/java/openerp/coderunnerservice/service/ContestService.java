package openerp.coderunnerservice.service;

import lombok.AllArgsConstructor;
import openerp.coderunnerservice.entity.ContestEntity;
import openerp.coderunnerservice.repo.ContestRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ContestService {

    private ContestRepo contestRepo;

    private static final String HASH_CONTEST = "CONTEST";
    private static final String HASH_CONTEST_SOLVING_DETAIL = "CONTEST_SOLVING_DETAIL";

    @Cacheable(value = HASH_CONTEST, key = "#contestId")
    public ContestEntity findContestWithCache(String contestId) {
        return findContest(contestId);
    }

    public ContestEntity findContest(String contestId) {
        return contestRepo.findContestByContestId(contestId);
    }

}
