package com.hust.openerp.taskmanagement.service.implement;

import com.hust.openerp.taskmanagement.dto.form.TaskForm;
import com.hust.openerp.taskmanagement.dto.form.TaskStatusForm;
import com.hust.openerp.taskmanagement.entity.*;
import com.hust.openerp.taskmanagement.repository.*;
import com.hust.openerp.taskmanagement.service.*;
import com.hust.openerp.taskmanagement.specification.TaskSpecification;
import com.hust.openerp.taskmanagement.specification.builder.GenericSpecificationsBuilder;
import com.hust.openerp.taskmanagement.util.CriteriaParser;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Log4j2
@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class TaskServiceImplement implements TaskService {

    private final TaskRepository taskRepository;

    private final StatusItemRepository statusItemRepo;

    private TaskAssignmentRepository taskAssignmentRepository;

    private ProjectService projectService;

    private TaskCategoryService taskCategoryService;

    private TaskPriorityService taskPriorityService;

    private TaskExecutionRepository taskExecutionRepository;

    private ProjectMemberService projectMemberService;

    private TaskSkillRepository taskSkillRepository;

    private UserSkillRepository userLoginSkillRepository;

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
        return taskRepository.findById(taskId).orElse(null);
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
        task.setStatusId(taskStatusForm.getStatusId());
        task.setDueDate(taskStatusForm.getDueDate());
        Task taskRes = taskRepository.save(task);

        TaskAssignment taskAssignment = taskAssignmentRepository.getByTaskId(taskId);
        String oldAssigneeId = taskAssignment.getAssigneeId();
        if (!taskAssignment.getAssigneeId().equals(taskStatusForm.getAssignee())) {
            taskAssignment.setAssigneeId(taskStatusForm.getAssignee());
        }
        taskAssignmentRepository.save(taskAssignment);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String assignee = taskStatusForm.getAssignee();
        String dueDate = sdf.format(taskStatusForm.getDueDate());
        TaskExecution taskExecution = new TaskExecution();
        taskExecution.setTask(taskRes);
        taskExecution.setExecutionTags("updated");
        taskExecution.setCreatedByUserId(userLoginId);
        taskExecution.setProjectId(task.getProject().getId());
        if (!taskStatusForm.getStatusId().equals(oldStatusItem.getStatusId())) {
            taskExecution.setStatus(statusItem.getDescription());
        }

        if (!oldAssigneeId.equals(taskStatusForm.getAssignee())) {
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
        TaskCategory taskCategory = taskCategoryService.getTaskCategory(taskForm.getCategoryId());
        String assignee = taskForm.getAssigneeId();
        String temp = taskForm.getAttachmentPaths().split(",")[0];
        String fileName = !temp.equals("") ? temp : "Không có tệp đính kèm";

        Task task = this.getTask(taskId);

        TaskAssignment taskAssignment = taskAssignmentRepository.getByTaskId(taskId);
        String oldAssigneeId = taskAssignment.getAssigneeId();
        log.info("TaskAssignment {}", taskAssignment);
        if (!taskAssignment.getAssigneeId().equals(assignee)) {
            taskAssignment.setAssigneeId(assignee);
            taskAssignmentRepository.save(taskAssignment);
        }

        TaskExecution taskExecution = new TaskExecution();
        taskExecution.setTask(task);
        taskExecution.setCreatedByUserId(createdByUserLoginId);
        taskExecution.setExecutionTags("updated");
        taskExecution.setProjectId(taskForm.getProjectId());

        if (!taskForm.getCategoryId().equals(task.getTaskCategory().getCategoryId())) {
            taskExecution.setCategory(taskCategory.getCategoryName());
        }

        if (!taskForm.getName().equals(task.getName())) {
            taskExecution.setTaskName(taskForm.getName());
        }

        if (!taskForm.getDescription().equals(task.getDescription())) {
            taskExecution.setTaskDescription(taskForm.getDescription());
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        if (!sdf.format(taskForm.getDueDate()).equals(sdf.format(task.getDueDate()))) {
            taskExecution.setDueDate(sdf.format(taskForm.getDueDate()));
        }

        if (taskForm.getStatusId() != null && task.getStatusItem() != null
                && !taskForm.getStatusId().equals(task.getStatusItem().getStatusId())) {
            taskExecution.setStatus(statusItem.getDescription());
        }

        if (!oldAssigneeId.equals(taskForm.getAssigneeId())) {
            taskExecution.setAssignee(assignee);
        }

        if (!taskForm.getPriorityId().equals(task.getTaskPriority().getPriorityId())) {
            taskExecution.setPriority(taskPriorityService
                    .getTaskPriorityById(taskForm.getPriorityId())
                    .getPriorityName());
        }

        if (!taskForm.getAttachmentPaths().equals(task.getAttachmentPaths())) {
            taskExecution.setFileName(fileName);
        }
        taskExecutionRepository.save(taskExecution);

        task.setName(taskForm.getName());
        task.setDescription(taskForm.getDescription());
        task.setAttachmentPaths(taskForm.getAttachmentPaths());
        task.setDueDate(taskForm.getDueDate());
        task.setProjectId(taskForm.getProjectId());
        task.setCategoryId(taskForm.getCategoryId());
        task.setStatusId(taskForm.getStatusId());
        task.setPriorityId(taskForm.getPriorityId());
        task.setCreatedByUserId(createdByUserLoginId);

        return taskRepository.save(task);
    }

    @Override
    public void addTaskSkill(UUID taskId, String skillId) {
        TaskSkill taskSkill = new TaskSkill();
        taskSkill.setTaskId(taskId);
        taskSkill.setSkillId(skillId);
        taskSkillRepository.save(taskSkill);
    }

    @Override
    public List<User> suggestAssignTask(UUID projectId, List<String> skillIds) {
        List<User> userList = projectMemberService.getMemberIdJoinedProject(projectId);
        List<User> personRs = new ArrayList<>();
        List<String> userLoginRs = new ArrayList<>();
        List<String> userIds = new ArrayList<>();
        HashMap<String, Integer> rs = new HashMap<>();
        HashMap<String, List<String>> collection = new HashMap<>();
        for (int i = 0; i < userList.size(); i++) {
            userIds
                    .add(userList.get(i).getId());
            rs.put(userIds.get(i), 0);
        }

        for (int i = 0; i < userIds.size(); i++) {
            List<UserSkill> userLoginSkills = userLoginSkillRepository.getListUserLoginSkill(userIds.get(i));
            List<String> tmp = new ArrayList<>();
            for (UserSkill userloginSkill : userLoginSkills) {
                tmp.add(userloginSkill.getSkillId());
            }
            collection.put(userIds.get(i), tmp);
        }

        for (int i = 0; i < skillIds.size(); i++) {
            for (int j = 0; j < userIds.size(); j++) {
                int dem = rs.get(userIds.get(j));
                List<String> arrTmp = collection.get(userIds.get(j));
                for (int k = 0; k < arrTmp.size(); k++) {
                    if (skillIds.get(i).equals(arrTmp.get(k))) {
                        dem++;
                    }
                }
                rs.put(userIds.get(j), dem);
            }
        }

        Collection<Integer> rsArr = rs.values();
        int max = 0;
        for (Integer i : rsArr) {
            if (max < i) {
                max = i;
            }
        }

        if (max == 0) {
            return personRs;
        }

        for (Map.Entry<String, Integer> entry : rs.entrySet()) {
            if (entry.getValue() == max) {
                userLoginRs.add(entry.getKey());
            }
        }

        userList.stream().filter(user -> userLoginRs.contains(user.getId())).forEach(personRs::add);
        return personRs;
    }

    public Page<Task> getTasksAssignedToUser(Pageable pageable, String assignee, @Nullable String search) {
        // replace "assignee:value" if exist in search by "assignee:`assignee`"
        if (search != null && !search.equals("")) {
            if (search.contains("assignee:"))
                search = search.replaceAll("assignee:[^ ]+", "assignee:" + assignee);
            else
                search = search + " AND assignee:" + assignee;
        } else {
            search = "assignee:" + assignee;
        }

        // default sort by created date
        if (pageable.getSort() == null || pageable.getSort().isUnsorted()) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                    Sort.by("createdStamp").descending());
        }

        GenericSpecificationsBuilder<Task> builder = new GenericSpecificationsBuilder<>();
        var specs = builder.build(new CriteriaParser().parse(search), TaskSpecification::new);
        return taskRepository.findAll(specs, pageable);
    }
}
