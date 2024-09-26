package openerp.openerpresourceserver.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.entity.JobPost;
import openerp.openerpresourceserver.repo.JobPostRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Log4j2
@AllArgsConstructor(onConstructor_ = @Autowired)
@Service
public class JobPostImpl implements JobPostService {
    private JobPostRepo jobPostRepo;

    @Override
    public List<JobPost> getAll() {
        return jobPostRepo.findAll();
    }

    @Override
    public List<JobPost> getAllByUserId(String id) {
        return jobPostRepo.findByUserId(id);
    }

    @Override
    public JobPost getById(Integer id) {
        Optional<JobPost> jobPost = jobPostRepo.findById(id);

        if (jobPost.isEmpty()) {
            throw new NoSuchElementException("Not exist job post with id " + id);
        }
        return jobPost.get();
    }

    @Override
    public JobPost save(JobPost jobPost) {
        return jobPostRepo.save(jobPost);
    }

    @Override
    public void deleteById(Integer id) {
        jobPostRepo.deleteById(id);
    }
}
