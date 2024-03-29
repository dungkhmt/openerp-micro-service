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
        application.setAssignStatus("PENDING");
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
    public List<Application> getApplicationByApplicationStatusAndSemester(String applicationStatus, String semester) {
        return applicationRepo.findByApplicationStatusAndSemester(applicationStatus, semester);
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

    // HAVEN'T CHECK THIS
    @Override
    public Application updateAssignStatus(int id, String status) {
        if(!("CANCELED".equals(status) || "APPROVED".equals(status) || "PENDING".equals(status))) {
            throw new IllegalArgumentException("Invalid status");
        }
        else {
            // Find that application by id
            Optional<Application> application = applicationRepo.findById(id);
            if(application.isEmpty()) {
                throw new IllegalArgumentException("Application with id " + id + " did not exist");
            }
            Application updateApplication = application.get();
            // If status is approved then check
            if ("APPROVED".equals(status)) {
                int startPeriod = updateApplication.getClassCall().getStartPeriod();
                int endPeriod = updateApplication.getClassCall().getEndPeriod();

                String semester = updateApplication.getClassCall().getSemester();

                // Search all the user application that's have been approved
                String userId = updateApplication.getUser().getId();
                List<Application> existedApplications = applicationRepo.findApplicationByUserIdAndAssignStatus(userId, "APPROVED", semester);

                // If !null then
                if (!existedApplications.isEmpty()) {
                    // Query each one, to find if the time is conflict, if yes throw error
                    for (Application app : existedApplications) {
                        int existingStartPeriod = app.getClassCall().getStartPeriod();
                        int existingEndPeriod = app.getClassCall().getEndPeriod();

                        // Check if there is an overlap in time periods
                        if (!(endPeriod <= existingStartPeriod || startPeriod >= existingEndPeriod)) {
                            throw new IllegalArgumentException("Time conflict with existing approved application");
                        }
                    }
                }
            }

            updateApplication.setAssignStatus(status);
            applicationRepo.save(updateApplication);
            return updateApplication;
        }
    }
}
