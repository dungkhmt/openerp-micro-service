package openerp.openerpresourceserver.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.entity.Application;
import openerp.openerpresourceserver.entity.ClassCall;
import openerp.openerpresourceserver.entity.User;
import openerp.openerpresourceserver.repo.ApplicationRepo;
import openerp.openerpresourceserver.repo.ClassCallRepo;
import openerp.openerpresourceserver.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Log4j2
@AllArgsConstructor(onConstructor_ = @Autowired)
@Service
public class ApplicationServiceImpl implements ApplicationService{

    private ApplicationRepo applicationRepo;
    private UserRepo userRepo;
    private ClassCallRepo classCallRepo;
    @Override
    public Application createApplication(Application application) {
        Optional<User> existUser = userRepo.findById(application.getUser().getId());
        if (existUser.isEmpty()) {
            throw new IllegalArgumentException("User with ID " + application.getUser().getId() + " not found");
        }
        Optional<ClassCall> existClassCall = classCallRepo.findById(application.getClassCall().getId());
        if(existClassCall.isEmpty()) {
            throw new IllegalArgumentException("Class with ID " + application.getClassCall().getId() + " not found");
        }
        application.setApplicationStatus("PENDING");
        application.setAssignStatus("NONE");
        applicationRepo.save(application);
        return application;
    }

    @Override
    public List<Application> getMyApplications(String userId) {
        return applicationRepo.findByUserId(userId);
    }

    @Override
    public List<Application> getApplicationByClassId(int classCallId) {
        return applicationRepo.findByClassCallId(classCallId);
    }

    @Override
    public List<Application> getUniqueApplicator() {
        return applicationRepo.findDistinctApplicationsByUser();
    }

    @Override
    public List<Application> getApplicationBySemester(String semester) {
        return applicationRepo.findApplicationsByClassSemester(semester);
    }

    @Override
    public Application updateApplicationStatus(int id, String status) {
        if (!("PENDING".equals(status) || "APPROVED".equals(status) || "REJECTED".equals(status))) {
            throw new IllegalArgumentException("Invalid status");
        }
        else {
            Optional<Application> application = applicationRepo.findById(id);
            if(application.isEmpty()) {
                throw new IllegalArgumentException("Application with id " + id + " did not exist");
            }
            Application existApplication = application.get();
            existApplication.setApplicationStatus(status);
            applicationRepo.save(existApplication);
            return existApplication;
        }
    }
}
