package com.hust.baseweb.applications.taskmanagement.service.implement;

import com.hust.baseweb.applications.taskmanagement.dto.dao.PersonDao;
import com.hust.baseweb.applications.taskmanagement.dto.form.TaskForm;
import com.hust.baseweb.applications.taskmanagement.dto.form.TaskStatusForm;
import com.hust.baseweb.applications.taskmanagement.entity.*;
import com.hust.baseweb.applications.taskmanagement.repository.*;
import com.hust.baseweb.applications.taskmanagement.service.*;
import com.hust.baseweb.entity.Person;
import com.hust.baseweb.entity.StatusItem;
import com.hust.baseweb.repo.UserLoginRepo;
import com.hust.baseweb.service.PersonService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hust.baseweb.repo.StatusItemRepo;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class TaskServiceImplement implements TaskService {

    private final TaskRepository taskRepository;

    private final StatusItemRepo statusItemRepo;

    private TaskAssignmentRepository taskAssignmentRepository;

    private ProjectService projectService;

    private TaskCategoryService taskCategoryService;

    private TaskPriorityService taskPriorityService;

    private TaskExecutionRepository taskExecutionRepository;

    private ProjectMemberService projectMemberService;

    private TaskSkillRepository taskSkillRepository;

    private PersonService personService;

    private UserLoginRepo userLoginRepo;

    private UserLoginSkillRepository userLoginSkillRepository;

    @Override
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public List<Task> getAllTaskInProject(UUID projectId) {
        return taskRepository.findAllTasksByProjectId(projectId);
    }

    @Override
    public List<Object[]> getTaskStaticsCategoryInProject(UUID projectId) {
        return taskRepository.getTaskStaticsCategoryInProject(projectId);
    }

    @Override
    public List<Object[]> getTaskStaticsStatusInProject(UUID projectId) {
        return taskRepository.getTaskStaticsStatusInProject(projectId);
    }

    @Override
    public StatusItem getStatusItemByStatusId(String statusId) {
        return statusItemRepo.findByStatusId(statusId);
    }

    @Override
    public Task getTask(UUID taskId) {
        return taskRepository.getOne(taskId);
    }

    @Override
    public Task updateTasks(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public Task updateStatusTask(UUID taskId, TaskStatusForm taskStatusForm, String userLoginId) {
        Task task = this.getTask(taskId);
        Date oldDueDate = task.getDueDate();
        StatusItem oldStatusItem = task.getStatusItem();
        StatusItem statusItem = this.getStatusItemByStatusId(taskStatusForm.getStatusId());
        task.setStatusItem(statusItem);
        task.setDueDate(taskStatusForm.getDueDate());
        Task taskRes = taskRepository.save(task);

        TaskAssignment taskAssignment = taskAssignmentRepository.getByTaskId(taskId);
        UUID partyIdOld = taskAssignment.getPartyId();
        if (!taskAssignment.getPartyId().toString().equals(taskStatusForm.getPartyId().toString())) {
            taskAssignment.setPartyId(taskStatusForm.getPartyId());
        }
        taskAssignmentRepository.save(taskAssignment);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String assignee = projectMemberService.getUserLoginByPartyId(taskStatusForm.getPartyId()).getUserLoginId();
        String dueDate = sdf.format(taskStatusForm.getDueDate());
        TaskExecution taskExecution = new TaskExecution();
        taskExecution.setTask(taskRes);
        taskExecution.setExecutionTags("updated");
        taskExecution.setCreatedByUserLoginId(userLoginId);
        taskExecution.setProjectId(task.getProject().getId());
        if (!taskStatusForm.getStatusId().equals(oldStatusItem.getStatusId())) {
            taskExecution.setStatus(statusItem.getDescription());
        }

        if (!partyIdOld.toString().equals(taskStatusForm.getPartyId().toString())) {
            taskExecution.setAssignee(assignee);
        }

        if (!sdf.format(taskStatusForm.getDueDate()).equals(sdf.format(oldDueDate))) {
            taskExecution.setDueDate(dueDate);
        }

        taskExecutionRepository.save(taskExecution);
        return taskRes;
    }

    @Override
    public Task updateTask(UUID taskId, TaskForm taskForm, String createdByUserLoginId) {
        StatusItem statusItem = this.getStatusItemByStatusId(taskForm.getStatusId());
        TaskPriority taskPriority = taskPriorityService.getTaskPriorityById(taskForm.getPriorityId());
        TaskCategory taskCategory = taskCategoryService.getTaskCategory(taskForm.getCategoryId());
        String assignee = projectMemberService.getUserLoginByPartyId(taskForm.getPartyId()).getUserLoginId();
        String temp = taskForm.getAttachmentPaths().split(",")[0];
        String fileName = !temp.equals("") ? temp : "Không có tệp đính kèm";

        Task task = this.getTask(taskId);
        Task oldTask = new Task(task);
        task.setName(taskForm.getName());
        task.setDescription(taskForm.getDescription());
        task.setAttachmentPaths(taskForm.getAttachmentPaths());
        task.setDueDate(taskForm.getDueDate());
        task.setProject(projectService.getProjectById(taskForm.getProjectId()));
        task.setStatusItem(statusItem);
        task.setTaskCategory(taskCategory);
        task.setTaskPriority(taskPriority);
        task.setCreatedByUserLoginId(createdByUserLoginId);

        Task taskRes = taskRepository.save(task);

        TaskAssignment taskAssignment = taskAssignmentRepository.getByTaskId(taskId);
        UUID oldPartyId = taskAssignment.getPartyId();
        if (taskAssignment.getPartyId() != taskForm.getPartyId()) {
            taskAssignment.setPartyId(taskForm.getPartyId());
        }
        taskAssignmentRepository.save(taskAssignment);

        TaskExecution taskExecution = new TaskExecution();
        taskExecution.setTask(taskRes);
        taskExecution.setCreatedByUserLoginId(createdByUserLoginId);
        taskExecution.setExecutionTags("updated");
        taskExecution.setProjectId(taskForm.getProjectId());

        if (!taskForm.getCategoryId().equals(oldTask.getTaskCategory().getCategoryId())) {
            taskExecution.setCategory(taskCategory.getCategoryName());
        }

        if (!taskForm.getName().equals(oldTask.getName())) {
            taskExecution.setTaskName(taskForm.getName());
        }

        if (!taskForm.getDescription().equals(oldTask.getDescription())) {
            taskExecution.setTaskDescription(taskForm.getDescription());
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        if (!sdf.format(taskForm.getDueDate()).equals(sdf.format(oldTask.getDueDate()))) {
            taskExecution.setDueDate(sdf.format(taskForm.getDueDate()));
        }

        if (!taskForm.getStatusId().equals(oldTask.getStatusItem().getStatusId())) {
            taskExecution.setStatus(statusItem.getDescription());
        }

        if (!oldPartyId.toString().equals(taskForm.getPartyId().toString())) {
            taskExecution.setAssignee(assignee);
        }

        if (!taskForm.getPriorityId().equals(oldTask.getTaskPriority().getPriorityId())) {
            taskExecution.setPriority(taskPriorityService
                                          .getTaskPriorityById(taskForm.getPriorityId())
                                          .getPriorityName());
        }

        if (!taskForm.getAttachmentPaths().equals(oldTask.getAttachmentPaths())) {
            taskExecution.setFileName(fileName);
        }
        taskExecutionRepository.save(taskExecution);
        return taskRepository.save(taskRes);
    }

    @Override
    public void addTaskSkill(UUID taskId, String skillId) {
        TaskSkill taskSkill = new TaskSkill();
        taskSkill.setTaskId(taskId);
        taskSkill.setSkillId(skillId);
        taskSkillRepository.save(taskSkill);
    }

    @Override
    public List<PersonDao> suggestAssignTask(UUID projectId, List<String> skillIds) {
        List<Person> personList = projectMemberService.getMemberIdJoinedProject(projectId);
        List<PersonDao> personRs = new ArrayList<>();
        List<String> userLoginRs = new ArrayList<>();
        List<String> userLoginIds = new ArrayList<>();
        HashMap<String, Integer> rs = new HashMap<>();
        HashMap<String, List<String>> collection = new HashMap<>();
        for(int i=0; i<personList.size(); i++){
            userLoginIds.add(projectMemberService.getUserLoginByPartyId(personList.get(i).getPartyId()).getUserLoginId());
            rs.put(userLoginIds.get(i), 0);
        }

        for(int i=0; i<userLoginIds.size(); i++){
            String userLoginId = userLoginIds.get(i);
            List<UserloginSkill> userLoginSkills = userLoginSkillRepository.getListUserLoginSkill(userLoginIds.get(i));
            List<String> tmp = new ArrayList<>();
            for(UserloginSkill userloginSkill: userLoginSkills){
                tmp.add(userloginSkill.getBacklogSkillId());
            }
            collection.put(userLoginIds.get(i), tmp);
        }

        for(int i=0; i<skillIds.size(); i++){
            for(int j=0; j<userLoginIds.size(); j++){
                int dem = rs.get(userLoginIds.get(j));
                List<String> arrTmp = collection.get(userLoginIds.get(j));
                for(int k=0; k<arrTmp.size(); k++){
                    if(skillIds.get(i).equals(arrTmp.get(k))){
                        dem++;
                    }
                }
                rs.put(userLoginIds.get(j), dem);
            }
        }

        Collection<Integer> rsArr = rs.values();
        int max = 0;
        for (Integer i: rsArr){
            if(max < i){
                max = i;
            }
        }

        if(max == 0){
            return personRs;
        }

        for(Map.Entry<String, Integer> entry : rs.entrySet()){
            if(entry.getValue() == max){
                userLoginRs.add(entry.getKey());
            }
        }

        for(String userLoginId: userLoginRs){
            personRs.add(new PersonDao(personService.findByPartyId(userLoginRepo.findByUserLoginId(userLoginId).getParty().getPartyId()), userLoginId));
        }
        return personRs;
    }
}
