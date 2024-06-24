package openerp.openerpresourceserver.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.entity.*;
import openerp.openerpresourceserver.repo.CVEducationRepo;
import openerp.openerpresourceserver.repo.UserRepo;
import openerp.openerpresourceserver.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/employee-cv")
public class EmployeeCVController {
    private EmployeeCVService employeeCVService;
    private EducationService educationService;
    private ExperienceService experienceService;
    private SkillService skillService;
    private CVEducationService cvEducationService;
    private UserService userService;
    private CVSkillService cvSkillService;
    private CVWorkingExperienceService cvWorkingExperienceService;
    private FirebaseService firebaseService;
    @GetMapping("user/{userId}")
    ResponseEntity<?> getAllUserCV(@PathVariable String userId) {
        List<EmployeeCV> employeeCVList = employeeCVService.getAllByUserId(userId);
        List<Map<String, Object>> CVLists = new ArrayList<>();
        for (EmployeeCV employeeCVi : employeeCVList) {
            EmployeeCV employeeCV = employeeCVService.getById(employeeCVi.getId());
            List<CVEducation> cvEducationList = cvEducationService.getAllByCVId(employeeCV.getId());
            List<CVWorkingExperience> cvWorkingExperienceList = cvWorkingExperienceService.getAllByCVId(employeeCV.getId());
            List<CVSkill> cvSkillList = cvSkillService.getAllByCVId(employeeCV.getId());
            List<Education> educations = new ArrayList<>();
            for (CVEducation cvEducation : cvEducationList) {
                Integer cvEducationId = cvEducation.getEducationId();
                Education education = educationService.getById(cvEducationId);
                educations.add(education);
            }

            List<Experience> experiences = new ArrayList<>();
            for (CVWorkingExperience cvWorkingExperience : cvWorkingExperienceList) {
                Experience experience = experienceService.getById(cvWorkingExperience.getWorkingExperienceId());
                experiences.add(experience);
            }

            List<Skill> skills = new ArrayList<>();
            for (CVSkill cvSkill : cvSkillList) {
                Skill skill = skillService.getById(cvSkill.getSkillId());
                skills.add(skill);
            }

            Map<String, Object> cvDetails = new HashMap<>();
            cvDetails.put("employeeCV", employeeCV);
            cvDetails.put("experiences", experiences);
            cvDetails.put("educations", educations);
            cvDetails.put("skills", skills);
            CVLists.add(cvDetails);
        }
        return  ResponseEntity.ok(CVLists);
}
    @GetMapping("/{id}")
    ResponseEntity<?> getCV(@PathVariable Integer id) {
        EmployeeCV employeeCV = employeeCVService.getById(id);
        List<CVEducation> cvEducationList = cvEducationService.getAllByCVId(employeeCV.getId());
        List<CVWorkingExperience> cvWorkingExperienceList = cvWorkingExperienceService.getAllByCVId(employeeCV.getId());
        List<CVSkill> cvSkillList = cvSkillService.getAllByCVId(employeeCV.getId());

        List<Education> educations = new ArrayList<>();
        for(CVEducation cvEducation : cvEducationList) {
            Integer cvEducationId = cvEducation.getId();
            Education education = educationService.getById(cvEducationId);
            educations.add(education);
        }

        List<Experience> experiences = new ArrayList<>();
        for(CVWorkingExperience cvWorkingExperience : cvWorkingExperienceList) {
            Experience experience = experienceService.getById(cvWorkingExperience.getId());
            experiences.add(experience);
        }

        List<Skill> skills = new ArrayList<>();
        for(CVSkill cvSkill : cvSkillList) {
            Skill skill = skillService.getById(cvSkill.getId());
            skills.add(skill);
        }

        Map<String, Object> cvDetails = new HashMap<>();
        cvDetails.put("employeeCV", employeeCV);
        cvDetails.put("experiences", experiences);
        cvDetails.put("educations", educations);
        cvDetails.put("skills", skills);

        return ResponseEntity.ok(cvDetails);
    }


    @PostMapping
    ResponseEntity<?> save(@RequestBody Map<String, Object> data) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        List<LinkedHashMap<String, Object>> educationsPrototype = (List<LinkedHashMap<String, Object>>) data.get("educations");
        List<LinkedHashMap<String, Object>> experiencesPrototype = (List<LinkedHashMap<String, Object>>) data.get("experiences");
        List<LinkedHashMap<String, Object>> skillsPrototype = (List<LinkedHashMap<String, Object>>) data.get("skills");
        // from prototype to object
        User user =  mapper.convertValue(data.get("user"), User.class);
        EmployeeCV employeeCV = mapper.convertValue(data.get("employeeCV"), EmployeeCV.class);
        // cast with list data type
        List<Education> educations = new ArrayList<>();
        for (Map<String, Object> education : educationsPrototype) {
            Education education1 = mapper.convertValue(education, Education.class);
            if(education1.getId() == null) {
                education1.setUser(user);
                education1.setId(educationService.save(education1).getId());
            }
            educations.add(education1);

        }

        List<Experience> experiences = new ArrayList<>();
        for (Map<String, Object> experience : experiencesPrototype) {
            Experience experience1 = mapper.convertValue(experience, Experience.class);
            if(experience1.getId() == null) {
                experience1.setUser(user);
                experience1.setId(experienceService.save(experience1).getId());
            }
            experiences.add(experience1);
        }

        List<Skill> skills = new ArrayList<>();
        for (Map<String, Object> skill : skillsPrototype) {
            Skill skill1 = mapper.convertValue(skill, Skill.class);
            if(skill1.getId() == null) {
                skill1.setUser(user);
                skill1.setId(skillService.save(skill1).getId());
            }
            skills.add(skill1);
        }
        // end casting
//        System.out.println(experiences);
//        System.out.println(skills);
//        System.out.println(educations);
        employeeCV.setUser(user);
        EmployeeCV newEmployeeCV = employeeCVService.save(employeeCV);
        Integer cvId = newEmployeeCV.getId();

        List<CVEducation> cvEducationList = new ArrayList<>();
        for(Education education : educations) {
            CVEducation cvEducation = new CVEducation();
            cvEducation.setEducationId(education.getId());
            cvEducation.setCvId(cvId);
            cvEducationList.add(cvEducationService.save(cvEducation));
        }
        List<CVWorkingExperience> cvWorkingExperienceList =  new ArrayList<>();
        for(Experience experience : experiences) {
            CVWorkingExperience  cvExperience = new CVWorkingExperience();
            cvExperience.setCvId(cvId);
            cvExperience.setWorkingExperienceId(experience.getId());
            System.out.println(cvExperience);
            cvWorkingExperienceList.add(cvWorkingExperienceService.save(cvExperience));
        }
        List<CVSkill> cvSkillList =  new ArrayList<>();
        for(Skill skill : skills) {
            CVSkill cvSkill = new CVSkill();
            cvSkill.setCvId(cvId);
            cvSkill.setSkillId(skill.getId());
            cvSkillList.add(cvSkillService.save(cvSkill));
        }

        Map<String, Object> cvDetails = new HashMap<>();
        cvDetails.put("educations", cvEducationList);
        cvDetails.put("experiences", cvWorkingExperienceList);
        cvDetails.put("skills", cvSkillList);

        return ResponseEntity.ok(cvDetails);

    }

    @PostMapping("/file-upload")
    ResponseEntity<?> saveFile(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        System.out.println("xxxxxxxx" + multipartFile);
        String fileUrl = firebaseService.uploadFile(multipartFile);

        return ResponseEntity.ok(fileUrl);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        employeeCVService.deleteById(id);
        return ResponseEntity.ok("cv with " + id + " was deleted");
    }

}
